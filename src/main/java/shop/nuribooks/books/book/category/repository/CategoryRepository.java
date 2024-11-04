package shop.nuribooks.books.book.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.category.entitiy.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);

	List<Category> findAllByParentCategoryIsNull();

	Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);
}
