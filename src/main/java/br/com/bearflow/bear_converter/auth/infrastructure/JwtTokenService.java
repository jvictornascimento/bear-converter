package br.com.bearflow.bear_converter.auth.infrastructure;

import br.com.bearflow.bear_converter.users.domain.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Instant;

@Service
public class JwtTokenService {

	private final Algorithm algorithm;
	private final Clock clock;
	private final long accessTokenMinutes;

	@Autowired
	public JwtTokenService(PrivateKey privateKey, PublicKey publicKey, Clock clock, JwtTokenProperties properties) {
		this(privateKey, publicKey, clock, properties.accessTokenMinutes());
	}

	public JwtTokenService(PrivateKey privateKey, PublicKey publicKey, Clock clock, long accessTokenMinutes) {
		this.algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, (RSAPrivateKey) privateKey);
		this.clock = clock;
		this.accessTokenMinutes = accessTokenMinutes;
	}

	public String createAccessToken(User user) {
		Instant now = clock.instant();
		return JWT.create()
			.withSubject(user.getId().toString())
			.withClaim("email", user.getEmail())
			.withClaim("role", user.getRole().name())
			.withIssuedAt(now)
			.withExpiresAt(now.plusSeconds(accessTokenSeconds()))
			.sign(algorithm);
	}

	public DecodedJWT verify(String token) {
		return JWT.require(algorithm)
			.build()
			.verify(token);
	}

	public long accessTokenSeconds() {
		return accessTokenMinutes * 60;
	}
}
