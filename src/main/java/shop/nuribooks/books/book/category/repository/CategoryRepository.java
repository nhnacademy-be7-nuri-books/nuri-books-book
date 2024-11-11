package shop.nuribooks.books.book.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.nuribooks.books.book.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);

	List<Category> findAllByParentCategoryIsNull();

	Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);

	@Query(value = """
		WITH RECURSIVE CategoryTree AS (
		SELECT id, parent_category_id
		FROM category
		WHERE id = :categoryId
		UNION ALL
			SELECT c.id, c.parent_category_id
			FROM category c
				JOIN CategoryTree ct ON c.parent_category_id = ct.id
			)
		SELECT id FROM CategoryTree
		""", nativeQuery = true)
	List<Long> findAllChildCategoryIds(@Param("categoryId") Long categoryId);
}
