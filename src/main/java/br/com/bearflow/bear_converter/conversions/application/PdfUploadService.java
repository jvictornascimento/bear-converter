package br.com.bearflow.bear_converter.conversions.application;

import br.com.bearflow.bear_converter.conversions.api.dto.PdfUploadResponse;
import br.com.bearflow.bear_converter.conversions.domain.PdfComplexityLevel;
import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;
import br.com.bearflow.bear_converter.conversions.domain.PdfInspectionResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

@Service
public class PdfUploadService {

	private static final long MAX_PDF_SIZE_BYTES = 10 * 1024 * 1024;
	private static final byte[] PDF_SIGNATURE = new byte[] {'%', 'P', 'D', 'F', '-'};
	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "application/x-pdf");
	private static final String PDF_ACCEPTED_MESSAGE = "PDF accepted for conversion";
	private static final String IMAGE_PDF_NOT_SUPPORTED_MESSAGE = "PDF image model is not available yet";
	private static final String PDF_COMPLEXITY_NOT_SUPPORTED_MESSAGE = "PDF complexity is not available for the free plan yet";

	private final PdfInspectionService pdfInspectionService;

	public PdfUploadService(PdfInspectionService pdfInspectionService) {
		this.pdfInspectionService = pdfInspectionService;
	}

	public PdfUploadResponse upload(MultipartFile file) {
		validateFile(file);
		try {
			PdfInspectionResult inspectionResult = pdfInspectionService.inspect(file.getBytes());
			if (inspectionResult.documentType() == PdfDocumentType.IMAGE) {
				throw new ImagePdfNotSupportedException(IMAGE_PDF_NOT_SUPPORTED_MESSAGE);
			}
			if (!isAvailableForFreePlan(inspectionResult.complexityLevel())) {
				throw new PdfComplexityNotSupportedException(PDF_COMPLEXITY_NOT_SUPPORTED_MESSAGE);
			}
			return new PdfUploadResponse(
				file.getOriginalFilename(),
				inspectionResult.documentType(),
				inspectionResult.complexityLevel(),
				PDF_ACCEPTED_MESSAGE
			);
		} catch (IOException exception) {
			throw new InvalidPdfUploadException("Invalid PDF file");
		}
	}

	private boolean isAvailableForFreePlan(PdfComplexityLevel complexityLevel) {
		return complexityLevel == PdfComplexityLevel.SIMPLE || complexityLevel == PdfComplexityLevel.MEDIUM;
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new InvalidPdfUploadException("PDF file is required");
		}
		if (file.getSize() > MAX_PDF_SIZE_BYTES) {
			throw new InvalidPdfUploadException("PDF file exceeds the maximum size of 10MB");
		}
		String fileName = file.getOriginalFilename();
		if (fileName == null || fileName.isBlank() || fileName.contains("/") || fileName.contains("\\")) {
			throw new InvalidPdfUploadException("Invalid PDF file name");
		}
		if (!fileName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
			throw new InvalidPdfUploadException("Only PDF files are accepted");
		}
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
			throw new InvalidPdfUploadException("Only PDF content type is accepted");
		}
		validatePdfSignature(file);
	}

	private void validatePdfSignature(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			byte[] header = inputStream.readNBytes(PDF_SIGNATURE.length);
			if (header.length < PDF_SIGNATURE.length) {
				throw new InvalidPdfUploadException("Invalid PDF signature");
			}
			for (int index = 0; index < PDF_SIGNATURE.length; index++) {
				if (header[index] != PDF_SIGNATURE[index]) {
					throw new InvalidPdfUploadException("Invalid PDF signature");
				}
			}
		} catch (IOException exception) {
			throw new InvalidPdfUploadException("Invalid PDF file");
		}
	}
}
