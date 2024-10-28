package shop.nuribooks.books.book.bookstate.service;

import java.util.List;

import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;

public interface BookStateService {
	void registerState(BookStateRequest bookStateRequest);
	BookStateResponse getBookState(Integer id);
	List<BookStateResponse> getAllBookStates();
	void updateState(Integer id, BookStateRequest bookStateRequest);
	void deleteState(Integer id);
}
