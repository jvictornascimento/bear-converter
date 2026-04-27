package br.com.bearflow.bear_converter.auth.domain;

import br.com.bearflow.bear_converter.users.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 120)
	private String token;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(nullable = false)
	private boolean revoked;

	protected RefreshToken() {
	}

	private RefreshToken(String token, User user, Instant expiresAt) {
		this.token = token;
		this.user = user;
		this.expiresAt = expiresAt;
		this.revoked = false;
	}

	public static RefreshToken create(String token, User user, Instant expiresAt) {
		return new RefreshToken(token, user, expiresAt);
	}

	public boolean isActive(Instant now) {
		return !revoked && expiresAt.isAfter(now);
	}

	public void revoke() {
		this.revoked = true;
	}

	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setIdForTest(Long id) {
		this.id = id;
	}
}
