package shop.nuribooks.books.book.bookstate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.bookstate.entitiy.BookState;

public interface BookStateRepository extends JpaRepository<BookState, Integer> {
	boolean existsBookStatesByDetail(String detail);
}
