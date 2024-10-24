package shop.nuribooks.books.repository.bookstate;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.BookState;

public interface BookStateRepository extends JpaRepository<BookState, Long> {
}
