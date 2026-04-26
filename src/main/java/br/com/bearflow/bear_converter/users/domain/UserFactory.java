package br.com.bearflow.bear_converter.users.domain;

public final class UserFactory {

	private UserFactory() {
	}

	public static User createLocalUser(String name, String email, String passwordHash) {
		return new User(name, email, passwordHash, AuthProvider.LOCAL, null, UserRole.USER);
	}
}
