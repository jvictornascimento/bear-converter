package br.com.bearflow.bear_converter.cadmodel.report;

public record ConversionIssue(
	ConversionIssueSeverity severity,
	String code,
	String message
) {
}
