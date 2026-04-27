package br.com.bearflow.bear_converter.conversions.api;

import br.com.bearflow.bear_converter.conversions.api.dto.PdfUploadResponse;
import br.com.bearflow.bear_converter.conversions.application.PdfUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/conversions")
public class PdfUploadController {

	private final PdfUploadService pdfUploadService;

	public PdfUploadController(PdfUploadService pdfUploadService) {
		this.pdfUploadService = pdfUploadService;
	}

	@PostMapping("/pdf")
	public ResponseEntity<PdfUploadResponse> upload(@RequestPart("file") MultipartFile file) {
		return ResponseEntity.status(HttpStatus.ACCEPTED)
			.body(pdfUploadService.upload(file));
	}
}
