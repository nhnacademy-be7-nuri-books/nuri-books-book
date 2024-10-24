package shop.nuribooks.books.service.bookstate;

import java.util.List;

import shop.nuribooks.books.dto.bookstate.BookStateRequest;
import shop.nuribooks.books.dto.bookstate.BookStateResponse;
import shop.nuribooks.books.dto.member.ResponseMessage;

public interface BookStateService {
	void registerState(String adminId, BookStateRequest bookStateRequest);
	List<BookStateResponse> getAllBooks();
	void updateState(Integer id, BookStateRequest bookStateRequest);
}
