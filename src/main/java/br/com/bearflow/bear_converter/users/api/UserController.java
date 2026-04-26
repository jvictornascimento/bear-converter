package br.com.bearflow.bear_converter.users.api;

import br.com.bearflow.bear_converter.users.api.dto.AdminUpdateUserRequest;
import br.com.bearflow.bear_converter.users.api.dto.CreateUserRequest;
import br.com.bearflow.bear_converter.users.api.dto.UserPageResponse;
import br.com.bearflow.bear_converter.users.api.dto.UserResponse;
import br.com.bearflow.bear_converter.users.application.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userService.create(request));
	}

	@GetMapping("/admin/users")
	public UserPageResponse list(@RequestParam(required = false) Boolean active, Pageable pageable) {
		return UserPageResponse.from(userService.list(active, pageable));
	}

	@GetMapping("/admin/users/{id}")
	public UserResponse getById(@PathVariable Long id) {
		return userService.getById(id);
	}

	@PatchMapping("/admin/users/{id}")
	public UserResponse update(@PathVariable Long id, @Valid @RequestBody AdminUpdateUserRequest request) {
		return userService.update(id, request);
	}

	@DeleteMapping("/admin/users/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/admin/users/{id}/restore")
	public UserResponse restore(@PathVariable Long id) {
		return userService.restore(id);
	}
}
