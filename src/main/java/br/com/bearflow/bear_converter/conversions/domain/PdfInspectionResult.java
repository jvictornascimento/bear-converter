package br.com.bearflow.bear_converter.conversions.domain;

import br.com.bearflow.bear_converter.cadmodel.drawing.Drawing;

public record PdfInspectionResult(
	PdfDocumentType documentType,
	PdfComplexityLevel complexityLevel,
	Drawing drawing
) {
}
