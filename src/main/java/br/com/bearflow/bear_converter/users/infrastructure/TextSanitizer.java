package br.com.bearflow.bear_converter.users.infrastructure;

import br.com.bearflow.bear_converter.users.application.UnsafeTextException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TextSanitizer {

	public String normalizeName(String value) {
		String normalizedValue = normalizeSpaces(value);
		ensureSafeText(normalizedValue);
		return normalizedValue;
	}

	public String normalizeEmail(String value) {
		return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
	}

	private String normalizeSpaces(String value) {
		return value == null ? null : value.trim().replaceAll("\\s+", " ");
	}

	private void ensureSafeText(String value) {
		if (value == null) {
			return;
		}
		String lowerValue = value.toLowerCase(Locale.ROOT);
		if (value.contains("<") || value.contains(">") || lowerValue.contains("script")) {
			throw new UnsafeTextException("Unsafe text value");
		}
		for (int index = 0; index < value.length(); index++) {
			char current = value.charAt(index);
			if (Character.isISOControl(current)) {
				throw new UnsafeTextException("Unsafe text value");
			}
		}
	}
}
