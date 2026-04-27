package br.com.bearflow.bear_converter.cadmodel.entity;

import java.util.List;

public record GroupEntity(String layerName, List<CadEntity> entities) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.GROUP;
	}
}
