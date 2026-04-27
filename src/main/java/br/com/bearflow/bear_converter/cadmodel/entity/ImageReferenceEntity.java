package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.BoundingBox;

public record ImageReferenceEntity(String layerName, BoundingBox bounds, boolean relevant) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.IMAGE_REFERENCE;
	}
}
