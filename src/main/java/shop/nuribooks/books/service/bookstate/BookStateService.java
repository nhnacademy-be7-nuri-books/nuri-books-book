package shop.nuribooks.books.service.bookstate;

import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.member.ResponseMessage;

public interface BookStateService {
	void registerState(BookStateRequest bookStateRequest);
}
