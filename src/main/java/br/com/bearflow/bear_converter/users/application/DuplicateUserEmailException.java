package br.com.bearflow.bear_converter.users.application;

public class DuplicateUserEmailException extends RuntimeException {

	public DuplicateUserEmailException(String message) {
		super(message);
	}
}
