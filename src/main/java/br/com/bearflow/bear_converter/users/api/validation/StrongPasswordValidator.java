package br.com.bearflow.bear_converter.users.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 20;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
			return false;
		}
		boolean hasLowercase = false;
		boolean hasUppercase = false;
		boolean hasNumber = false;
		boolean hasSpecialCharacter = false;

		for (int index = 0; index < value.length(); index++) {
			char current = value.charAt(index);
			if (Character.isLowerCase(current)) {
				hasLowercase = true;
			} else if (Character.isUpperCase(current)) {
				hasUppercase = true;
			} else if (Character.isDigit(current)) {
				hasNumber = true;
			} else {
				hasSpecialCharacter = true;
			}
		}
		return hasLowercase && hasUppercase && hasNumber && hasSpecialCharacter;
	}
}
