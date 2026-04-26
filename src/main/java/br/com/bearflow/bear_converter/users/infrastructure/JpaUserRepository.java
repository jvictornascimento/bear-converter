package br.com.bearflow.bear_converter.users.infrastructure;

import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

	@Override
	@Query("select user from User user where (:active is null or user.active = :active)")
	Page<User> findAllByActive(@Param("active") Boolean active, Pageable pageable);
}
