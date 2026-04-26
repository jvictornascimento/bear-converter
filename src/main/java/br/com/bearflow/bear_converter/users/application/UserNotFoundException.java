package br.com.bearflow.bear_converter.users.application;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("User not found: " + id);
	}
}
