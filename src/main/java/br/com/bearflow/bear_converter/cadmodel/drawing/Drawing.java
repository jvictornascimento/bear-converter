package br.com.bearflow.bear_converter.cadmodel.drawing;

import br.com.bearflow.bear_converter.cadmodel.entity.CadEntity;
import br.com.bearflow.bear_converter.cadmodel.layer.Layer;
import br.com.bearflow.bear_converter.cadmodel.report.ConversionReport;
import br.com.bearflow.bear_converter.cadmodel.style.LineStyle;
import br.com.bearflow.bear_converter.cadmodel.style.TextStyle;

import java.util.List;

public record Drawing(
	DrawingMetadata metadata,
	DrawingUnits units,
	List<DrawingPage> pages,
	List<Layer> layers,
	List<LineStyle> lineStyles,
	List<TextStyle> textStyles,
	List<CadEntity> entities,
	ConversionReport conversionReport
) {
}
