package shop.nuribooks.books.book.coupon.enums;

import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CouponType {
	ALL, BOOK, CATEGORY;

	@JsonCreator
	public static CouponType fromValue(String value) {

		if (Objects.isNull(value)) {
			return null;
		}

		return Stream.of(CouponType.values())
			.filter(couponType -> couponType.getValue().equals(value.toUpperCase()))
			.findFirst()
			.orElse(null);
	}

	@JsonValue
	public String getValue() {
		return name();
	}
}
