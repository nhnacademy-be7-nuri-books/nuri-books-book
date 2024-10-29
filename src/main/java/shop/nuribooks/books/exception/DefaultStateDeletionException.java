package shop.nuribooks.books.exception;

public class DefaultStateDeletionException extends BadRequestException {
	public DefaultStateDeletionException() {
		super("기본 상태는 삭제할 수 없습니다.");
	}
}
