package shop.nuribooks.books.book.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.response.BookListResponse;
import shop.nuribooks.books.book.book.dto.response.TopBookResponse;
import shop.nuribooks.books.book.book.entity.Book;

public interface BookCustomRepository {
	List<BookListResponse> findAllWithPublisher(Pageable pageable);

	Optional<Book> findBookByIdAndDeletedAtIsNull(Long bookId);

	List<Book> findAllAndDeletedAtIsNull();

	long countBook();

	List<TopBookResponse> findTopBooksByLikes();

	List<TopBookResponse> findTopBooksByScore();
}
