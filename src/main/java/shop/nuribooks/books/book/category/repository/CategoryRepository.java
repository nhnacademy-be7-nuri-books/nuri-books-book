package shop.nuribooks.books.book.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);

	boolean existsByNameAndParentCategoryId(String name, Long parentCategoryId);

	List<Category> findAllByParentCategoryIsNull();

	Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);
}
