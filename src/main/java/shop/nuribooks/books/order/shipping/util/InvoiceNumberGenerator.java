package shop.nuribooks.books.order.shipping.util;

import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceNumberGenerator {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateInvoiceNumber() {
		return generateSegment(4) + "-" + generateSegment(4) + "-" + generateSegment(5);
	}

	private static String generateSegment(int length) {
		StringBuilder segment = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			segment.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
		}
		return segment.toString();
	}
}

