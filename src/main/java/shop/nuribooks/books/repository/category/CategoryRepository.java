package shop.nuribooks.books.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.book.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);
}