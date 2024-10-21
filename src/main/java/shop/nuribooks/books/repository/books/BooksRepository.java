package shop.nuribooks.books.repository.books;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.Books;

public interface BooksRepository extends JpaRepository<Books, Long> {
}
