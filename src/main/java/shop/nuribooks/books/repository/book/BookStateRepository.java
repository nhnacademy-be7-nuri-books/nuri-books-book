package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.validation.constraints.NotNull;
import shop.nuribooks.books.entity.book.BookStates;
import shop.nuribooks.books.entity.book.enums.BookStatesEnum;

public interface BookStateRepository extends JpaRepository<BookStates, Integer> {
	boolean existsBookStatesByDetail(BookStatesEnum detail);
}
