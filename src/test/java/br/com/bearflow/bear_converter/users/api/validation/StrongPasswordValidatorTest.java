package br.com.bearflow.bear_converter.users.api.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrongPasswordValidatorTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void shouldAcceptStrongPassword() {
		PasswordRequest request = new PasswordRequest("Strong1!");

		assertThat(validator.validate(request)).isEmpty();
	}

	@Test
	void shouldRejectPasswordWithoutLowercaseLetter() {
		PasswordRequest request = new PasswordRequest("STRONG1!");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void shouldRejectPasswordWithoutUppercaseLetter() {
		PasswordRequest request = new PasswordRequest("strong1!");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void shouldRejectPasswordWithoutNumber() {
		PasswordRequest request = new PasswordRequest("Strong!!");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void shouldRejectPasswordWithoutSpecialCharacter() {
		PasswordRequest request = new PasswordRequest("Strong12");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void shouldRejectPasswordWithLessThanEightCharacters() {
		PasswordRequest request = new PasswordRequest("Str1!");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	@Test
	void shouldRejectPasswordWithMoreThanTwentyCharacters() {
		PasswordRequest request = new PasswordRequest("StrongPassword123456!");

		assertThat(validator.validate(request)).isNotEmpty();
	}

	private record PasswordRequest(
		@StrongPassword
		String password
	) {
	}
}
