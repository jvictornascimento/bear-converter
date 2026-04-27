package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

public record TextEntity(String layerName, Point2 insertionPoint, String value) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.TEXT;
	}
}
