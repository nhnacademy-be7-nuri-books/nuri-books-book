package shop.nuribooks.books.book.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
	boolean existsByBookAndCategory(Book book, Category category);

	Optional<BookCategory> findByBookAndCategory(Book book, Category category);

}
