package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.dimension.DimensionType;

public record DimensionEntity(String layerName, DimensionType dimensionType, String value) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.DIMENSION;
	}
}
