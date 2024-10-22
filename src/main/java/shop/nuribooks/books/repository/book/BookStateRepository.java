package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.BookStates;

public interface BookStateRepository extends JpaRepository<BookStates, Long> {
}
