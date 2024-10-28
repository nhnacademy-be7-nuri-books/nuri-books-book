package shop.nuribooks.books.book.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.category.entitiy.BookCategory;
import shop.nuribooks.books.book.category.entitiy.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
	boolean existsByBookAndCategory(Book book, Category category);
}
