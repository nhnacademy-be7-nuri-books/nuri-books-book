package shop.nuribooks.books.repository.book;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.Books;

public interface BookRepository extends JpaRepository<Books, Long> {
	boolean existsByIsbn(String isbn);
}
