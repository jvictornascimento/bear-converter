package br.com.bearflow.bear_converter.auth.application;

public class InvalidRefreshTokenException extends RuntimeException {

	public InvalidRefreshTokenException(String message) {
		super(message);
	}
}
