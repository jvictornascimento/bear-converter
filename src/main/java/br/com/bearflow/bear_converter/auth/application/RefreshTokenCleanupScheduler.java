package br.com.bearflow.bear_converter.auth.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCleanupScheduler {

	private final AuthService authService;

	public RefreshTokenCleanupScheduler(AuthService authService) {
		this.authService = authService;
	}

	@Scheduled(fixedDelayString = "${security.jwt.refresh-token-cleanup-delay-ms:3600000}")
	public void revokeExpiredRefreshTokens() {
		authService.revokeExpiredTokens();
	}
}
