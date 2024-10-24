package shop.nuribooks.books.repository.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.nuribooks.books.entity.book.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);

	List<Category> findAllByParentCategoryIsNull();

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategory WHERE c.parentCategory IS NULL")
	List<Category> findAllTopCategoriesWithChildren();
}
