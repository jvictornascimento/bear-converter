package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

import java.math.BigDecimal;

public record ArcEntity(
	String layerName,
	Point2 center,
	BigDecimal radius,
	BigDecimal startAngle,
	BigDecimal endAngle
) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.ARC;
	}
}
