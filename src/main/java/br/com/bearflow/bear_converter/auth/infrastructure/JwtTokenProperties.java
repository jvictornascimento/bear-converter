package br.com.bearflow.bear_converter.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtTokenProperties(
	long accessTokenMinutes,
	long refreshTokenDays,
	String privateKey,
	String publicKey
) {
}
