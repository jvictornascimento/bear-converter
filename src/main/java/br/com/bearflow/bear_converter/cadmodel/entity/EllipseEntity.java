package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

import java.math.BigDecimal;

public record EllipseEntity(String layerName, Point2 center, BigDecimal radiusX, BigDecimal radiusY) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.ELLIPSE;
	}
}
