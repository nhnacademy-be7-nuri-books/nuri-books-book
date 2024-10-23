package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
}
