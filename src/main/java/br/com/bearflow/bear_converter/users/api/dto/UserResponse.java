package br.com.bearflow.bear_converter.users.api.dto;

import br.com.bearflow.bear_converter.users.domain.AuthProvider;
import br.com.bearflow.bear_converter.users.domain.User;
import br.com.bearflow.bear_converter.users.domain.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
	Long id,
	String name,
	String email,
	AuthProvider provider,
	UserRole role,
	boolean active,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	LocalDateTime deletedAt
) {

	public static UserResponse from(User user) {
		return new UserResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getProvider(),
			user.getRole(),
			user.isActive(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			user.getDeletedAt()
		);
	}
}
