package shop.nuribooks.books.book.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookListResponse;
import shop.nuribooks.books.book.book.entity.Book;

public interface BookRepositoryCustom {
	List<BookListResponse> findAllWithPublisher(Pageable pageable);

	Optional<Book> findBookByIdAndDeletedAtIsNull(Long bookId);

	long countBook();
}
