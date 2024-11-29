package shop.nuribooks.books.book.point.enums;

public enum PolicyName {
	IMAGE_REVIEW("이미지 리뷰"),
	REVIEW("글 리뷰"),
	WELCOME("회원가입"),
	USING("사용"),
	REFUND("환불"),
	SAVE("적립"),
	CANCEL("취소");
	String korName;

	PolicyName(String korName) {
		this.korName = korName;
	}

	public String toString() {
		return this.korName;
	}
}
