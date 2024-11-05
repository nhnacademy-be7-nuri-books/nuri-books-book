package shop.nuribooks.books.book.book.entitiy;

import shop.nuribooks.books.exception.book.InvalidBookStateException;

public enum BookStateEnum {
	NORMAL_DISTRIBUTION("정상판매"),
	PREORDER("예약판매"),
	NEW("신간"),
	NOT_PUBLISHED("미출간"),
	OUT_OF_PRINT("절판"),
	SOLD_OUT("품절"),
	USED("중고");

	private final String korName;

	BookStateEnum(String korName) {
		this.korName = korName;
	}

	public String getKorName() {
		return korName;
	}

	//enum에 존재하는 값 비교를 위한 메서드
	public static BookStateEnum fromString(String name) {
		try {
			return BookStateEnum.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidBookStateException(name);
		}
	}
}

