package shop.nuribooks.books.exception;

public class InvalidPageRequestException extends BadRequestException {
	public InvalidPageRequestException() {
		super("조회 가능한 페이지 범위를 벗어났습니다.");
	}
}
