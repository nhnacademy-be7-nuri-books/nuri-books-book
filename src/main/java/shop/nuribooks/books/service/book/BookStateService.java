package shop.nuribooks.books.service.book;

import shop.nuribooks.books.dto.book.request.BookStateReq;
import shop.nuribooks.books.dto.common.ResponseMessage;

public interface BookStateService {
	ResponseMessage registerState(BookStateReq bookStateReq);
}
