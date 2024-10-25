package shop.nuribooks.books.book.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.category.entitiy.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
