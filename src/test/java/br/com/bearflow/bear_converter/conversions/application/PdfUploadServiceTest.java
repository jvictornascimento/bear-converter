package br.com.bearflow.bear_converter.conversions.application;

import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;
import br.com.bearflow.bear_converter.conversions.domain.PdfComplexityLevel;
import br.com.bearflow.bear_converter.conversions.support.PdfTestFiles;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfUploadServiceTest {

	private final PdfUploadService pdfUploadService = new PdfUploadService(new PdfInspectionService());

	@Test
	void shouldAcceptVectorPdfForConversion() throws Exception {
		var file = new MockMultipartFile("file", "electrical.pdf", "application/pdf", PdfTestFiles.vectorPdf());

		var response = pdfUploadService.upload(file);

		assertThat(response.fileName()).isEqualTo("electrical.pdf");
		assertThat(response.documentType()).isEqualTo(PdfDocumentType.VECTOR);
		assertThat(response.complexityLevel()).isEqualTo(PdfComplexityLevel.SIMPLE);
		assertThat(response.message()).isEqualTo("PDF accepted for conversion");
	}

	@Test
	void shouldRejectImagePdfUntilServiceIsAvailable() throws Exception {
		var file = new MockMultipartFile("file", "scan.pdf", "application/pdf", PdfTestFiles.imagePdf());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(ImagePdfNotSupportedException.class)
			.hasMessage("PDF image model is not available yet");
	}

	@Test
	void shouldRejectEmptyPdfUpload() {
		var file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("PDF file is required");
	}

	@Test
	void shouldRejectFileWithoutPdfExtension() {
		var file = new MockMultipartFile("file", "drawing.txt", "text/plain", "not a pdf".getBytes());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Only PDF files are accepted");
	}

	@Test
	void shouldRejectPdfWithInvalidContentType() throws Exception {
		var file = new MockMultipartFile("file", "drawing.pdf", "text/plain", PdfTestFiles.vectorPdf());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Only PDF content type is accepted");
	}

	@Test
	void shouldRejectPdfWithoutPdfSignature() {
		var file = new MockMultipartFile("file", "drawing.pdf", "application/pdf", "not a pdf".getBytes());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Invalid PDF signature");
	}

	@Test
	void shouldRejectUnsafePdfFileName() throws Exception {
		var file = new MockMultipartFile("file", "../drawing.pdf", "application/pdf", PdfTestFiles.vectorPdf());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Invalid PDF file name");
	}

	@Test
	void shouldRejectPdfAboveSizeLimit() {
		byte[] content = new byte[(10 * 1024 * 1024) + 1];
		Arrays.fill(content, (byte) '0');
		content[0] = '%';
		content[1] = 'P';
		content[2] = 'D';
		content[3] = 'F';
		content[4] = '-';
		var file = new MockMultipartFile("file", "large.pdf", "application/pdf", content);

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("PDF file exceeds the maximum size of 10MB");
	}

	@Test
	void shouldRejectInvalidPdfContent() {
		var file = new MockMultipartFile("file", "drawing.pdf", "application/pdf", "%PDF-not-valid".getBytes());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Invalid PDF file");
	}

	@Test
	void shouldRejectVectorPdfThatNeedsPremiumFeatures() throws Exception {
		var file = new MockMultipartFile("file", "multi-page.pdf", "application/pdf", PdfTestFiles.multiPageVectorPdf());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(PdfComplexityNotSupportedException.class)
			.hasMessage("PDF complexity is not available for the free plan yet");
	}
}
