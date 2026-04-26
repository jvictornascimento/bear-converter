package br.com.bearflow.bear_converter.users.api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record UserPageResponse(
	List<UserResponse> content,
	long totalElements,
	int totalPages,
	int page,
	int size
) {

	public static UserPageResponse from(Page<UserResponse> page) {
		return new UserPageResponse(
			page.getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getNumber(),
			page.getSize()
		);
	}
}
