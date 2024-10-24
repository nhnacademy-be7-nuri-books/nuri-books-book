package shop.nuribooks.books.exception.bookstate;

import shop.nuribooks.books.entity.book.BookStateEnum;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;

public class BookStateDetailAlreadyExistException extends ResourceAlreadyExistException {
	public BookStateDetailAlreadyExistException(BookStateEnum detail) {
		super("입력한 도서상태명" + detail + "이 이미 존재합니다");
	}
}
