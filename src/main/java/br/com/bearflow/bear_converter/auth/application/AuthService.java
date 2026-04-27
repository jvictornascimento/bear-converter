package br.com.bearflow.bear_converter.auth.application;

import br.com.bearflow.bear_converter.auth.api.dto.LoginRequest;
import br.com.bearflow.bear_converter.auth.api.dto.RefreshTokenRequest;
import br.com.bearflow.bear_converter.auth.api.dto.TokenResponse;
import br.com.bearflow.bear_converter.auth.domain.RefreshToken;
import br.com.bearflow.bear_converter.auth.domain.RefreshTokenRepository;
import br.com.bearflow.bear_converter.auth.infrastructure.JwtTokenService;
import br.com.bearflow.bear_converter.users.domain.AuthProvider;
import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import br.com.bearflow.bear_converter.users.infrastructure.TextSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {

	private static final String TOKEN_TYPE = "Bearer";

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final TextSanitizer textSanitizer;
	private final JwtTokenService jwtTokenService;
	private final Clock clock;
	private final long refreshTokenDays;
	private final SecureRandom secureRandom = new SecureRandom();

	@Autowired
	public AuthService(
		UserRepository userRepository,
		RefreshTokenRepository refreshTokenRepository,
		PasswordEncoder passwordEncoder,
		TextSanitizer textSanitizer,
		JwtTokenService jwtTokenService,
		Clock clock,
		br.com.bearflow.bear_converter.auth.infrastructure.JwtTokenProperties properties
	) {
		this(userRepository, refreshTokenRepository, passwordEncoder, textSanitizer, jwtTokenService, clock, properties.refreshTokenDays());
	}

	public AuthService(
		UserRepository userRepository,
		RefreshTokenRepository refreshTokenRepository,
		PasswordEncoder passwordEncoder,
		TextSanitizer textSanitizer,
		JwtTokenService jwtTokenService,
		Clock clock,
		long refreshTokenDays
	) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.textSanitizer = textSanitizer;
		this.jwtTokenService = jwtTokenService;
		this.clock = clock;
		this.refreshTokenDays = refreshTokenDays;
	}

	@Transactional
	public TokenResponse login(LoginRequest request) {
		String email = textSanitizer.normalizeEmail(request.email());
		User user = userRepository.findByEmail(email)
			.filter(User::isActive)
			.filter(current -> current.getProvider() == AuthProvider.LOCAL)
			.filter(current -> passwordEncoder.matches(request.password(), current.getPasswordHash()))
			.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
		return createTokenResponse(user);
	}

	@Transactional
	public TokenResponse refresh(RefreshTokenRequest request) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
			.filter(current -> current.isActive(clock.instant()))
			.orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
		refreshToken.revoke();
		refreshTokenRepository.save(refreshToken);
		return createTokenResponse(refreshToken.getUser());
	}

	@Transactional
	public void logout(String refreshTokenValue) {
		refreshTokenRepository.findByToken(refreshTokenValue)
			.ifPresent(refreshToken -> {
				refreshToken.revoke();
				refreshTokenRepository.save(refreshToken);
			});
	}

	@Transactional
	public int revokeExpiredTokens() {
		var expiredTokens = refreshTokenRepository.findActiveExpiredTokens(clock.instant());
		expiredTokens.forEach(RefreshToken::revoke);
		expiredTokens.forEach(refreshTokenRepository::save);
		return expiredTokens.size();
	}

	private TokenResponse createTokenResponse(User user) {
		String accessToken = jwtTokenService.createAccessToken(user);
		String refreshTokenValue = createRefreshTokenValue();
		Instant expiresAt = clock.instant().plusSeconds(refreshTokenDays * 24 * 60 * 60);
		refreshTokenRepository.save(RefreshToken.create(refreshTokenValue, user, expiresAt));
		return new TokenResponse(accessToken, refreshTokenValue, TOKEN_TYPE, jwtTokenService.accessTokenSeconds());
	}

	private String createRefreshTokenValue() {
		byte[] tokenBytes = new byte[48];
		secureRandom.nextBytes(tokenBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
	}
}
