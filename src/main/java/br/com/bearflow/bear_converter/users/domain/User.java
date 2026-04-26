package br.com.bearflow.bear_converter.users.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false, unique = true, length = 180)
	private String email;

	@Column(name = "password_hash", length = 120)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private AuthProvider provider;

	@Column(name = "provider_id", length = 180)
	private String providerId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private UserRole role;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	protected User() {
	}

	User(String name, String email, String passwordHash, AuthProvider provider, String providerId, UserRole role) {
		this.name = name;
		this.email = email;
		this.passwordHash = passwordHash;
		this.provider = provider;
		this.providerId = providerId;
		this.role = role;
		this.active = true;
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public void updateByAdmin(String name, String email, UserRole role, boolean active) {
		this.name = name;
		this.email = email;
		this.role = role;
		if (active) {
			restore();
			return;
		}
		deactivate();
	}

	public void deactivate() {
		this.active = false;
		if (this.deletedAt == null) {
			this.deletedAt = LocalDateTime.now();
		}
	}

	public void restore() {
		this.active = true;
		this.deletedAt = null;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public AuthProvider getProvider() {
		return provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public UserRole getRole() {
		return role;
	}

	public boolean isActive() {
		return active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setIdForTest(Long id) {
		this.id = id;
	}
}
