package shop.nuribooks.books.repository.books;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.BookStates;

public interface BookStatesRepository extends JpaRepository<BookStates, Long> {
}
