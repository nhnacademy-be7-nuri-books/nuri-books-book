package shop.nuribooks.books.book.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;

public interface BookService {
	void registerBook(BaseBookRegisterRequest reqDto);

	Page<BookContributorsResponse> getBooks(Pageable pageable);
  
	BookResponse getBookById(Long bookId, boolean updateRecentView);

	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);

	void deleteBook(Long bookId);
}
