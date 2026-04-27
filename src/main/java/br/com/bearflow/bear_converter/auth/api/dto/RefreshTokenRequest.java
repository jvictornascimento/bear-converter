package br.com.bearflow.bear_converter.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
	@NotBlank
	String refreshToken
) {
}
