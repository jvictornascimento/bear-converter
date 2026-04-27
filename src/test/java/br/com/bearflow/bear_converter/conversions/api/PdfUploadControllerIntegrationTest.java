package br.com.bearflow.bear_converter.conversions.api;

import br.com.bearflow.bear_converter.conversions.support.PdfTestFiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class PdfUploadControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser
	void shouldUploadVectorPdf() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"electrical.pdf",
			"application/pdf",
			PdfTestFiles.vectorPdf()
		);

		mockMvc.perform(multipart("/api/v1/conversions/pdf").file(file))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.fileName").value("electrical.pdf"))
			.andExpect(jsonPath("$.documentType").value("VECTOR"))
			.andExpect(jsonPath("$.complexityLevel").value("SIMPLE"))
			.andExpect(jsonPath("$.message").value("PDF accepted for conversion"));
	}

	@Test
	@WithMockUser
	void shouldRejectImagePdf() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"scan.pdf",
			"application/pdf",
			PdfTestFiles.imagePdf()
		);

		mockMvc.perform(multipart("/api/v1/conversions/pdf").file(file))
			.andExpect(status().is(422))
			.andExpect(jsonPath("$.message").value("PDF image model is not available yet"));
	}

	@Test
	void shouldRequireAuthentication() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"electrical.pdf",
			"application/pdf",
			PdfTestFiles.vectorPdf()
		);

		mockMvc.perform(multipart("/api/v1/conversions/pdf").file(file))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser
	void shouldRejectPdfThatExceedsFreePlan() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"multi-page.pdf",
			"application/pdf",
			PdfTestFiles.multiPageVectorPdf()
		);

		mockMvc.perform(multipart("/api/v1/conversions/pdf").file(file))
			.andExpect(status().isUnprocessableContent())
			.andExpect(jsonPath("$.message").value("PDF complexity is not available for the free plan yet"));
	}
}
