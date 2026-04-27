package br.com.bearflow.bear_converter.auth.application;

import br.com.bearflow.bear_converter.auth.api.dto.LoginRequest;
import br.com.bearflow.bear_converter.auth.api.dto.RefreshTokenRequest;
import br.com.bearflow.bear_converter.auth.domain.RefreshToken;
import br.com.bearflow.bear_converter.auth.domain.RefreshTokenRepository;
import br.com.bearflow.bear_converter.auth.infrastructure.JwtTokenService;
import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserFactory;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import br.com.bearflow.bear_converter.users.infrastructure.TextSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

	private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-04-26T12:00:00Z"), ZoneOffset.UTC);

	private PasswordEncoder passwordEncoder;
	private InMemoryUserRepository userRepository;
	private InMemoryRefreshTokenRepository refreshTokenRepository;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		passwordEncoder = new BCryptPasswordEncoder();
		userRepository = new InMemoryUserRepository();
		refreshTokenRepository = new InMemoryRefreshTokenRepository();
		JwtTokenService jwtTokenService = new JwtTokenService(TestRsaKeys.privateKey(), TestRsaKeys.publicKey(), CLOCK, 15);
		authService = new AuthService(
			userRepository,
			refreshTokenRepository,
			passwordEncoder,
			new TextSanitizer(),
			jwtTokenService,
			CLOCK,
			7
		);
	}

	@Test
	void shouldLoginWithEmailAndPassword() {
		userRepository.save(UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!")));

		var response = authService.login(new LoginRequest("JOAO@EMAIL.COM", "SafePass1!"));

		assertThat(response.accessToken()).isNotBlank();
		assertThat(response.refreshToken()).isNotBlank();
		assertThat(response.tokenType()).isEqualTo("Bearer");
		assertThat(response.expiresInSeconds()).isEqualTo(900);
		assertThat(refreshTokenRepository.tokens).hasSize(1);
	}

	@Test
	void shouldRejectWrongPassword() {
		userRepository.save(UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!")));

		assertThatThrownBy(() -> authService.login(new LoginRequest("joao@email.com", "WrongPass1!")))
			.isInstanceOf(InvalidCredentialsException.class)
			.hasMessage("Invalid email or password");
	}

	@Test
	void shouldRejectInactiveUser() {
		User user = UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!"));
		user.deactivate();
		userRepository.save(user);

		assertThatThrownBy(() -> authService.login(new LoginRequest("joao@email.com", "SafePass1!")))
			.isInstanceOf(InvalidCredentialsException.class)
			.hasMessage("Invalid email or password");
	}

	@Test
	void shouldRefreshAccessTokenAndRotateRefreshToken() {
		User user = userRepository.save(UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!")));
		var login = authService.login(new LoginRequest(user.getEmail(), "SafePass1!"));

		var refreshed = authService.refresh(new RefreshTokenRequest(login.refreshToken()));

		assertThat(refreshed.accessToken()).isNotBlank();
		assertThat(refreshed.refreshToken()).isNotEqualTo(login.refreshToken());
		assertThat(refreshTokenRepository.findByToken(login.refreshToken()).orElseThrow().isRevoked()).isTrue();
	}

	@Test
	void shouldLogoutByRevokingRefreshToken() {
		User user = userRepository.save(UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!")));
		var login = authService.login(new LoginRequest(user.getEmail(), "SafePass1!"));

		authService.logout(login.refreshToken());

		assertThat(refreshTokenRepository.findByToken(login.refreshToken()).orElseThrow().isRevoked()).isTrue();
	}

	@Test
	void shouldRevokeExpiredRefreshTokens() {
		User user = userRepository.save(UserFactory.createLocalUser("Joao", "joao@email.com", passwordEncoder.encode("SafePass1!")));
		RefreshToken expiredToken = RefreshToken.create("expired", user, Instant.parse("2026-04-25T12:00:00Z"));
		refreshTokenRepository.save(expiredToken);

		int revokedCount = authService.revokeExpiredTokens();

		assertThat(revokedCount).isEqualTo(1);
		assertThat(expiredToken.isRevoked()).isTrue();
	}

	private static class InMemoryUserRepository implements UserRepository {

		private final List<User> users = new ArrayList<>();
		private long nextId = 1L;

		@Override
		public Optional<User> findByEmail(String email) {
			return users.stream()
				.filter(user -> user.getEmail().equals(email))
				.findFirst();
		}

		@Override
		public boolean existsByEmail(String email) {
			return findByEmail(email).isPresent();
		}

		@Override
		public boolean existsByEmailAndIdNot(String email, Long id) {
			return false;
		}

		@Override
		public Page<User> findAllByActive(Boolean active, Pageable pageable) {
			return new PageImpl<>(users);
		}

		@Override
		public User save(User user) {
			if (user.getId() == null) {
				user.setIdForTest(nextId++);
			}
			users.removeIf(current -> current.getId().equals(user.getId()));
			users.add(user);
			return user;
		}

		@Override
		public Optional<User> findById(Long id) {
			return users.stream()
				.filter(user -> user.getId().equals(id))
				.findFirst();
		}

		@Override
		public void deleteAll() {
			users.clear();
		}
	}

	private static class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

		private final List<RefreshToken> tokens = new ArrayList<>();
		private long nextId = 1L;

		@Override
		public RefreshToken save(RefreshToken refreshToken) {
			if (refreshToken.getId() == null) {
				refreshToken.setIdForTest(nextId++);
			}
			tokens.removeIf(current -> current.getId().equals(refreshToken.getId()));
			tokens.add(refreshToken);
			return refreshToken;
		}

		@Override
		public Optional<RefreshToken> findByToken(String token) {
			return tokens.stream()
				.filter(refreshToken -> refreshToken.getToken().equals(token))
				.findFirst();
		}

		@Override
		public List<RefreshToken> findActiveExpiredTokens(Instant now) {
			return tokens.stream()
				.filter(refreshToken -> !refreshToken.isRevoked())
				.filter(refreshToken -> refreshToken.getExpiresAt().isBefore(now) || refreshToken.getExpiresAt().equals(now))
				.toList();
		}

		@Override
		public void deleteAll() {
			tokens.clear();
		}
	}
}
