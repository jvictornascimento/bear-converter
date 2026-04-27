package br.com.bearflow.bear_converter.conversions.application;

import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;
import br.com.bearflow.bear_converter.conversions.support.PdfTestFiles;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

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
	void shouldRejectInvalidPdfContent() {
		var file = new MockMultipartFile("file", "drawing.pdf", "application/pdf", "not a pdf".getBytes());

		assertThatThrownBy(() -> pdfUploadService.upload(file))
			.isInstanceOf(InvalidPdfUploadException.class)
			.hasMessage("Invalid PDF file");
	}
}
