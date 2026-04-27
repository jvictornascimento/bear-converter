package br.com.bearflow.bear_converter.cadmodel.block;

import br.com.bearflow.bear_converter.cadmodel.entity.CadEntity;
import br.com.bearflow.bear_converter.cadmodel.entity.CadEntityType;
import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

public record SymbolBlockEntity(String layerName, String blockName, Point2 insertionPoint) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.SYMBOL_BLOCK;
	}
}
