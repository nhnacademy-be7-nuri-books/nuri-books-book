package shop.nuribooks.books.book.bookstate.service;

import java.util.List;

import shop.nuribooks.books.book.bookstate.dto.BookStateRequest;
import shop.nuribooks.books.book.bookstate.dto.BookStateResponse;

public interface BookStateService {
	void registerState(String adminId, BookStateRequest bookStateRequest);
	List<BookStateResponse> getAllBooks();
	void updateState(Integer id, BookStateRequest bookStateRequest);
	void deleteState(Integer id);
}
