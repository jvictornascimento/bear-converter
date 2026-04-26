package br.com.bearflow.bear_converter.users.application;

import br.com.bearflow.bear_converter.users.api.dto.AdminUpdateUserRequest;
import br.com.bearflow.bear_converter.users.api.dto.CreateUserRequest;
import br.com.bearflow.bear_converter.users.api.dto.UserResponse;
import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserFactory;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import br.com.bearflow.bear_converter.users.infrastructure.TextSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TextSanitizer textSanitizer;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TextSanitizer textSanitizer) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.textSanitizer = textSanitizer;
	}

	@Transactional
	public UserResponse create(CreateUserRequest request) {
		String name = textSanitizer.normalizeName(request.name());
		String email = textSanitizer.normalizeEmail(request.email());
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateUserEmailException("Email already in use");
		}
		String passwordHash = passwordEncoder.encode(request.password());
		User user = UserFactory.createLocalUser(name, email, passwordHash);
		return UserResponse.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public Page<UserResponse> list(Boolean active, Pageable pageable) {
		return userRepository.findAllByActive(active, pageable)
			.map(UserResponse::from);
	}

	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return UserResponse.from(findUser(id));
	}

	@Transactional
	public UserResponse update(Long id, AdminUpdateUserRequest request) {
		User user = findUser(id);
		String name = textSanitizer.normalizeName(request.name());
		String email = textSanitizer.normalizeEmail(request.email());
		if (userRepository.existsByEmailAndIdNot(email, id)) {
			throw new DuplicateUserEmailException("Email already in use");
		}
		user.updateByAdmin(name, email, request.role(), request.active());
		return UserResponse.from(userRepository.save(user));
	}

	@Transactional
	public void delete(Long id) {
		User user = findUser(id);
		user.deactivate();
		userRepository.save(user);
	}

	@Transactional
	public UserResponse restore(Long id) {
		User user = findUser(id);
		user.restore();
		return UserResponse.from(userRepository.save(user));
	}

	private User findUser(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
}
