package br.com.bearflow.bear_converter.cadmodel.drawing;

import java.time.Instant;

public record DrawingMetadata(
	String sourceFileName,
	String sourceFormat,
	Instant importedAt
) {
}
