package shop.nuribooks.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByNameAndParentCategoryIsNull(String name);
}
