package shop.nuribooks.books.book.book.service;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.common.message.PagedResponse;

public interface BookService {
	BookRegisterResponse registerBook(BookRegisterRequest reqDto);
	BookResponse getBookById(Long bookId);
	PagedResponse<BookContributorsResponse> getBooks(Pageable pageable);
	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);
	void deleteBook(Long bookId);
}
