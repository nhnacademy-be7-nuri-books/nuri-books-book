package shop.nuribooks.books.book.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
}
