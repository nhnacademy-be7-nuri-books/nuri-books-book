package shop.nuribooks.books.book.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.TopBookLikeResponse;

public interface BookService {
	void registerBook(BaseBookRegisterRequest reqDto);

	Page<BookContributorsResponse> getBooks(Pageable pageable);
  
	BookResponse getBookById(Long bookId, boolean updateRecentView);

	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);

	void deleteBook(Long bookId);

	List<TopBookLikeResponse> getTopBookLikes();

	List<BookResponse> getAllBooks();
}
