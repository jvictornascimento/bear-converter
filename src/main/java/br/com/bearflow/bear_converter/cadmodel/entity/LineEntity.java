package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

public record LineEntity(String layerName, Point2 start, Point2 end) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.LINE;
	}
}
