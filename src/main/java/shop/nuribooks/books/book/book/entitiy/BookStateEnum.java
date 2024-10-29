package shop.nuribooks.books.book.book.entitiy;

import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.book.InvalidBookStateException;

public enum BookStateEnum {
	NEW("신간"),
	NORMAL("정가판매"),
	OUT_OF_PRINT("절판"),
	SOLD_OUT("품절"),
	PREORDER("예약판매"),
	USED("중고");

	private final String korName;

	BookStateEnum(String korName) {
		this.korName = korName;
	}

	public String getKorName() {
		return korName;
	}

	public static BookStateEnum fromString(String name) {
		try {
			return BookStateEnum.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidBookStateException(name);
		}
	}
}

