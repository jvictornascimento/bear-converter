package br.com.bearflow.bear_converter.auth.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

	@Bean
	KeyPair keyPair(JwtTokenProperties properties) {
		if (hasKey(properties.privateKey()) && hasKey(properties.publicKey())) {
			return new KeyPair(parsePublicKey(properties.publicKey()), parsePrivateKey(properties.privateKey()));
		}
		return generateLocalKeyPair();
	}

	@Bean
	PrivateKey privateKey(KeyPair keyPair) {
		return keyPair.getPrivate();
	}

	@Bean
	PublicKey publicKey(KeyPair keyPair) {
		return keyPair.getPublic();
	}

	private boolean hasKey(String key) {
		return key != null && !key.isBlank();
	}

	private PrivateKey parsePrivateKey(String value) {
		try {
			String content = cleanPem(value);
			byte[] decoded = Base64.getDecoder().decode(content);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
			return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
		} catch (Exception exception) {
			throw new IllegalStateException("Invalid RSA private key", exception);
		}
	}

	private PublicKey parsePublicKey(String value) {
		try {
			String content = cleanPem(value);
			byte[] decoded = Base64.getDecoder().decode(content);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
			return KeyFactory.getInstance("RSA").generatePublic(keySpec);
		} catch (Exception exception) {
			throw new IllegalStateException("Invalid RSA public key", exception);
		}
	}

	private String cleanPem(String value) {
		return value
			.replace("-----BEGIN PRIVATE KEY-----", "")
			.replace("-----END PRIVATE KEY-----", "")
			.replace("-----BEGIN PUBLIC KEY-----", "")
			.replace("-----END PUBLIC KEY-----", "")
			.replaceAll("\\s", "");
	}

	private KeyPair generateLocalKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception exception) {
			throw new IllegalStateException("Could not generate local RSA key pair", exception);
		}
	}
}
