package br.com.bearflow.bear_converter.auth.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {

	RefreshToken save(RefreshToken refreshToken);

	Optional<RefreshToken> findByToken(String token);

	List<RefreshToken> findActiveExpiredTokens(Instant now);

	void deleteAll();
}
