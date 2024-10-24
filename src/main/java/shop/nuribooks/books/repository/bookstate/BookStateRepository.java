package shop.nuribooks.books.repository.bookstate;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.BookState;
import shop.nuribooks.books.entity.book.BookStateEnum;

public interface BookStateRepository extends JpaRepository<BookState, Integer> {
	boolean existsBookStatesByDetail(String detail);
}
