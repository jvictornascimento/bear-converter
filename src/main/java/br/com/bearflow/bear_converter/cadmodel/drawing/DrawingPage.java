package br.com.bearflow.bear_converter.cadmodel.drawing;

import br.com.bearflow.bear_converter.cadmodel.geometry.BoundingBox;

public record DrawingPage(
	int number,
	BoundingBox bounds
) {
}
