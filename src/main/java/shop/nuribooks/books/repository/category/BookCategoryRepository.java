package shop.nuribooks.books.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
