package br.com.bearflow.bear_converter.users.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	Page<User> findAllByActive(Boolean active, Pageable pageable);

	User save(User user);

	Optional<User> findById(Long id);

	void deleteAll();
}
