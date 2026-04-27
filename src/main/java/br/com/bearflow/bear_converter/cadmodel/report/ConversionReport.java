package br.com.bearflow.bear_converter.cadmodel.report;

import java.util.List;

public record ConversionReport(
	List<ConversionIssue> issues
) {
}
