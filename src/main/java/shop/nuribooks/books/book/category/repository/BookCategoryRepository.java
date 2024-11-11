package shop.nuribooks.books.book.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.querydsl.BookCategoryCustom;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, BookCategoryCustom {
	boolean existsByBookAndCategory(Book book, Category category);

	Optional<BookCategory> findByBookAndCategory(Book book, Category category);
}
