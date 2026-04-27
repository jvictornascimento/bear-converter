package br.com.bearflow.bear_converter.auth.infrastructure;

import br.com.bearflow.bear_converter.auth.domain.RefreshToken;
import br.com.bearflow.bear_converter.auth.domain.RefreshTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepository {

	@Override
	@Query("select token from RefreshToken token where token.revoked = false and token.expiresAt <= :now")
	List<RefreshToken> findActiveExpiredTokens(@Param("now") Instant now);
}
