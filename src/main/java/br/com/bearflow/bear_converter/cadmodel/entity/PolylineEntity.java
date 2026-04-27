package br.com.bearflow.bear_converter.cadmodel.entity;

import br.com.bearflow.bear_converter.cadmodel.geometry.Point2;

import java.util.List;

public record PolylineEntity(String layerName, List<Point2> points, boolean closed) implements CadEntity {

	@Override
	public CadEntityType type() {
		return CadEntityType.POLYLINE;
	}
}
