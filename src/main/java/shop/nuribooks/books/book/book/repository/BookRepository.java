package shop.nuribooks.books.book.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.bookstate.entitiy.BookState;

public interface BookRepository extends JpaRepository<Book, Long> {
	boolean existsByIsbn(String isbn);
	List<Book> findByStateId(BookState stateId);
}
