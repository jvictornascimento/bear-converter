package br.com.bearflow.bear_converter.auth.application;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

final class TestRsaKeys {

	private static final KeyPair KEY_PAIR = createKeyPair();

	private TestRsaKeys() {
	}

	static PrivateKey privateKey() {
		return KEY_PAIR.getPrivate();
	}

	static PublicKey publicKey() {
		return KEY_PAIR.getPublic();
	}

	private static KeyPair createKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception exception) {
			throw new IllegalStateException("Could not create test RSA keys", exception);
		}
	}
}
