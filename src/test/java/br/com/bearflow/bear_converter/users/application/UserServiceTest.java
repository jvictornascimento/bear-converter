package br.com.bearflow.bear_converter.users.application;

import br.com.bearflow.bear_converter.users.api.dto.AdminUpdateUserRequest;
import br.com.bearflow.bear_converter.users.api.dto.CreateUserRequest;
import br.com.bearflow.bear_converter.users.domain.AuthProvider;
import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import br.com.bearflow.bear_converter.users.domain.UserRole;
import br.com.bearflow.bear_converter.users.infrastructure.TextSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

	private InMemoryUserRepository userRepository;
	private UserService userService;

	@BeforeEach
	void setUp() {
		userRepository = new InMemoryUserRepository();
		userService = new UserService(userRepository, new BCryptPasswordEncoder(), new TextSanitizer());
	}

	@Test
	void shouldCreateLocalUserWithNormalizedEmail() {
		CreateUserRequest request = new CreateUserRequest("  Maria   Silva  ", "  MARIA@EMAIL.COM ", "safePassword123");

		var response = userService.create(request);

		assertThat(response.name()).isEqualTo("Maria Silva");
		assertThat(response.email()).isEqualTo("maria@email.com");
		assertThat(response.role()).isEqualTo(UserRole.USER);
		assertThat(response.provider()).isEqualTo(AuthProvider.LOCAL);
		assertThat(response.active()).isTrue();
		assertThat(userRepository.users.getFirst().getPasswordHash()).isNotEqualTo("safePassword123");
	}

	@Test
	void shouldRejectDuplicateEmail() {
		userService.create(new CreateUserRequest("Maria Silva", "maria@email.com", "safePassword123"));

		assertThatThrownBy(() -> userService.create(new CreateUserRequest("Other User", "MARIA@EMAIL.COM", "safePassword123")))
			.isInstanceOf(DuplicateUserEmailException.class)
			.hasMessage("Email already in use");
	}

	@Test
	void shouldRejectUnsafeName() {
		assertThatThrownBy(() -> userService.create(new CreateUserRequest("<script>alert(1)</script>", "safe@email.com", "safePassword123")))
			.isInstanceOf(UnsafeTextException.class)
			.hasMessage("Unsafe text value");
	}

	@Test
	void shouldUpdateUser() {
		var created = userService.create(new CreateUserRequest("Maria Silva", "maria@email.com", "safePassword123"));
		AdminUpdateUserRequest request = new AdminUpdateUserRequest("Maria Admin", "admin@email.com", UserRole.ADMIN, true);

		var response = userService.update(created.id(), request);

		assertThat(response.name()).isEqualTo("Maria Admin");
		assertThat(response.email()).isEqualTo("admin@email.com");
		assertThat(response.role()).isEqualTo(UserRole.ADMIN);
	}

	@Test
	void shouldSoftDeleteAndRestoreUser() {
		var created = userService.create(new CreateUserRequest("Maria Silva", "maria@email.com", "safePassword123"));

		userService.delete(created.id());
		User deletedUser = userRepository.findById(created.id()).orElseThrow();
		assertThat(deletedUser.isActive()).isFalse();
		assertThat(deletedUser.getDeletedAt()).isNotNull();

		var restored = userService.restore(created.id());
		assertThat(restored.active()).isTrue();
		assertThat(userRepository.findById(created.id()).orElseThrow().getDeletedAt()).isNull();
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
			return users.stream()
				.anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(id));
		}

		@Override
		public org.springframework.data.domain.Page<User> findAllByActive(Boolean active, org.springframework.data.domain.Pageable pageable) {
			List<User> filteredUsers = users.stream()
				.filter(user -> active == null || user.isActive() == active)
				.toList();
			return new org.springframework.data.domain.PageImpl<>(filteredUsers, PageRequest.of(0, filteredUsers.size() == 0 ? 1 : filteredUsers.size()), filteredUsers.size());
		}

		@Override
		public User save(User entity) {
			if (entity.getId() == null) {
				entity.setIdForTest(nextId++);
			}
			users.removeIf(user -> user.getId().equals(entity.getId()));
			users.add(entity);
			return entity;
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
}
