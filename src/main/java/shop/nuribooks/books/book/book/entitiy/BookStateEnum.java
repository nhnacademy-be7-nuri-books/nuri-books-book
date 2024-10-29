package shop.nuribooks.books.book.book.entitiy;

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

	@Override
	public String toString() {
		return korName;
	}
}

