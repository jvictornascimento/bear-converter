package br.com.bearflow.bear_converter.users.api.dto;

import br.com.bearflow.bear_converter.users.api.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
	@NotBlank
	@Size(max = 120)
	String name,

	@NotBlank
	@Email
	@Size(max = 180)
	String email,

	@NotBlank
	@StrongPassword
	String password
) {

	public CreateUserRequest {
		name = name == null ? null : name.trim();
		email = email == null ? null : email.trim();
	}
}
