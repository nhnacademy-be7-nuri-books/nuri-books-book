package shop.nuribooks.books.repository.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Book;
import shop.nuribooks.books.entity.book.BookState;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
	List<Book> findByStateId(BookState stateId);
}
