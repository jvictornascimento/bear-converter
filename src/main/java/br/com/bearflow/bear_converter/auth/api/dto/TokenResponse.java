package br.com.bearflow.bear_converter.auth.api.dto;

public record TokenResponse(
	String accessToken,
	String refreshToken,
	String tokenType,
	long expiresInSeconds
) {
}
