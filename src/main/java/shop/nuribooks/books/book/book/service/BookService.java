package shop.nuribooks.books.book.book.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.TopBookLikeResponse;
import shop.nuribooks.books.common.message.PagedResponse;

public interface BookService {
	void registerBook(BaseBookRegisterRequest reqDto);

	BookResponse getBookById(Long bookId);

	PagedResponse<BookContributorsResponse> getBooks(Pageable pageable);

	void updateBook(Long bookId, BookUpdateRequest bookUpdateReq);

	void deleteBook(Long bookId);

	List<TopBookLikeResponse> getTopBookLikes();
}
