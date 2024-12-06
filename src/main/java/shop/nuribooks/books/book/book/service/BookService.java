package shop.nuribooks.books.book.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.request.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.request.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.response.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.response.BookResponse;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;

public interface BookService {
	void registerBook(BaseBookRegisterRequest reqDto);

	Page<BookContributorsResponse> getBooks(Pageable pageable);

	BookResponse getBookById(Long bookId, boolean updateRecentView);

	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);

	void deleteBook(Long bookId);

	List<TopBookResponse> getTopBookLikes();

	List<TopBookResponse> getTopBookScores();

	List<BookResponse> getAllBooks();
}
