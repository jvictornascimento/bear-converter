package br.com.bearflow.bear_converter.conversions.application;

public class InvalidPdfUploadException extends RuntimeException {

	public InvalidPdfUploadException(String message) {
		super(message);
	}
}
