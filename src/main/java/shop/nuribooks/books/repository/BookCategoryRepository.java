package shop.nuribooks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
