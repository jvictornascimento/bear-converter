package br.com.bearflow.bear_converter.conversions.api.dto;

import br.com.bearflow.bear_converter.conversions.domain.PdfComplexityLevel;
import br.com.bearflow.bear_converter.conversions.domain.PdfDocumentType;

public record PdfUploadResponse(
	String fileName,
	PdfDocumentType documentType,
	PdfComplexityLevel complexityLevel,
	String message
) {
}
