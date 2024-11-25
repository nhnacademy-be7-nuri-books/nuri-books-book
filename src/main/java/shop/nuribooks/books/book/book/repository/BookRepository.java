package shop.nuribooks.books.book.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>, BookCustomRepository {
	boolean existsByIsbn(String isbn);

	Optional<Book> findByIdAndDeletedAtIsNull(Long bookId);
}
