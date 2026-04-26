package br.com.bearflow.bear_converter.users.api.dto;

import br.com.bearflow.bear_converter.users.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminUpdateUserRequest(
	@NotBlank
	@Size(max = 120)
	String name,

	@NotBlank
	@Email
	@Size(max = 180)
	String email,

	@NotNull
	UserRole role,

	@NotNull
	Boolean active
) {

	public AdminUpdateUserRequest {
		name = name == null ? null : name.trim();
		email = email == null ? null : email.trim();
	}
}
