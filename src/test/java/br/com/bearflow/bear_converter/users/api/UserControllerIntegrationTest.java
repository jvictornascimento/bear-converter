package br.com.bearflow.bear_converter.users.api;

import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserFactory;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void shouldCreateUserWithSanitizedDataAndHashedPassword() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "  Joao   Nascimento  ",
					  "email": "  JOAO@EMAIL.COM  ",
					  "password": "SafePass1!"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").exists())
			.andExpect(jsonPath("$.name").value("Joao Nascimento"))
			.andExpect(jsonPath("$.email").value("joao@email.com"))
			.andExpect(jsonPath("$.role").value("USER"))
			.andExpect(jsonPath("$.provider").value("LOCAL"))
			.andExpect(jsonPath("$.active").value(true))
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.passwordHash").doesNotExist());

		User savedUser = userRepository.findByEmail("joao@email.com").orElseThrow();
		org.assertj.core.api.Assertions.assertThat(savedUser.getPasswordHash())
			.isNotEqualTo("SafePass1!")
			.isNotBlank();
	}

	@Test
	void shouldRejectUserWhenEmailAlreadyExists() throws Exception {
		userRepository.save(UserFactory.createLocalUser("Joao Nascimento", "joao@email.com", "hash"));

		mockMvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "Joao Nascimento",
					  "email": "joao@email.com",
					  "password": "SafePass1!"
					}
					"""))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Email already in use"));
	}

	@Test
	void shouldRejectUnsafeName() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "<script>alert('x')</script>",
					  "email": "user@email.com",
					  "password": "SafePass1!"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Invalid request data"));
	}

	@Test
	void shouldRejectWeakPassword() throws Exception {
		mockMvc.perform(post("/api/v1/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "Weak Password",
					  "email": "weak@email.com",
					  "password": "password"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Invalid request data"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldListUsersByActiveStatus() throws Exception {
		User activeUser = UserFactory.createLocalUser("Active User", "active@email.com", "hash");
		User inactiveUser = UserFactory.createLocalUser("Inactive User", "inactive@email.com", "hash");
		inactiveUser.deactivate();
		userRepository.save(activeUser);
		userRepository.save(inactiveUser);

		mockMvc.perform(get("/api/v1/admin/users?active=true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content", hasSize(1)))
			.andExpect(jsonPath("$.content[0].email").value("active@email.com"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldGetUserDetails() throws Exception {
		User user = userRepository.save(UserFactory.createLocalUser("Joao Nascimento", "joao@email.com", "hash"));

		mockMvc.perform(get("/api/v1/admin/users/{id}", user.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(user.getId()))
			.andExpect(jsonPath("$.email").value("joao@email.com"))
			.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldUpdateUserAsAdmin() throws Exception {
		User user = userRepository.save(UserFactory.createLocalUser("Old Name", "old@email.com", "hash"));

		mockMvc.perform(patch("/api/v1/admin/users/{id}", user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "name": "New Name",
					  "email": "new@email.com",
					  "role": "ADMIN",
					  "active": true
					}
					"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("New Name"))
			.andExpect(jsonPath("$.email").value("new@email.com"))
			.andExpect(jsonPath("$.role").value("ADMIN"))
			.andExpect(jsonPath("$.active").value(true));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldSoftDeleteUser() throws Exception {
		User user = userRepository.save(UserFactory.createLocalUser("Joao Nascimento", "joao@email.com", "hash"));

		mockMvc.perform(delete("/api/v1/admin/users/{id}", user.getId()))
			.andExpect(status().isNoContent());

		User deletedUser = userRepository.findById(user.getId()).orElseThrow();
		org.assertj.core.api.Assertions.assertThat(deletedUser.isActive()).isFalse();
		org.assertj.core.api.Assertions.assertThat(deletedUser.getDeletedAt()).isNotNull();
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldRestoreUser() throws Exception {
		User user = UserFactory.createLocalUser("Joao Nascimento", "joao@email.com", "hash");
		user.deactivate();
		user = userRepository.save(user);

		mockMvc.perform(patch("/api/v1/admin/users/{id}/restore", user.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.active").value(true));
	}
}
