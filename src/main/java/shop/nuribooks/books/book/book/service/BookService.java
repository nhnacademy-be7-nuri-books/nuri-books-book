package shop.nuribooks.books.book.book.service;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;

public interface BookService {
	BookRegisterResponse registerBook(BookRegisterRequest reqDto);
	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);
	void deleteBook(Long bookId);
}
