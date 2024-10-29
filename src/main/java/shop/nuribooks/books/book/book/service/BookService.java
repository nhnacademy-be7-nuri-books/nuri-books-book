package shop.nuribooks.books.book.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;

public interface BookService {
	BookRegisterResponse registerBook(BookRegisterRequest reqDto);
	BookResponse getBookById(Long bookId);
	Page<BookResponse> getBooks(Pageable pageable);
	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);
	void deleteBook(Long bookId);
}
