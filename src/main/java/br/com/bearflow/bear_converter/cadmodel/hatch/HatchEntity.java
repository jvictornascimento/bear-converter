package br.com.bearflow.bear_converter.cadmodel.hatch;

import br.com.bearflow.bear_converter.cadmodel.entity.CadEntity;
import br.com.bearflow.bear_converter.cadmodel.entity.CadEntityType;
import br.com.bearflow.bear_converter.cadmodel.entity.PolylineEntity;

import java.util.List;

public record HatchEntity(String layerName, String patternName, List<PolylineEntity> boundaries) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.HATCH;
	}
}
