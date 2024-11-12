package shop.nuribooks.books.book.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.common.config.QuerydslConfiguration;

/**
 * CategoryRepository의 기능을 테스트하는 클래스.
 */
@DataJpaTest
@Import(QuerydslConfiguration.class)
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	private Category parentCategory;
	private Category subCategory;

	@BeforeEach
	void setUp() {
		// 데이터 초기화
		parentCategory = Category.builder()
			.name("여행")
			.build();
		parentCategory = categoryRepository.save(parentCategory);

		subCategory = Category.builder()
			.name("국내 여행")
			.parentCategory(parentCategory)
			.build();
		subCategory = categoryRepository.save(subCategory);
	}

	@AfterEach
	void tearDown() {
		// 데이터 정리
		categoryRepository.deleteAll();
	}

	@Test
	void saveCategory() {
		// given
		Category category = Category.builder()
			.name("음악")
			.build();

		// when
		Category savedCategory = categoryRepository.save(category);

		// then
		assertThat(savedCategory).isNotNull();
		assertThat(savedCategory.getId()).isNotNull(); // 저장 후 ID가 생성되었는지 확인
		assertThat(savedCategory.getName()).isEqualTo("음악");
	}

	@Test
	void saveSubCategory() {
		// given
		Category newSubCategory = Category.builder()
			.name("클래식")
			.parentCategory(parentCategory)
			.build();

		// when
		Category savedSubCategory = categoryRepository.save(newSubCategory);

		// then
		assertThat(savedSubCategory).isNotNull();
		assertThat(savedSubCategory.getId()).isNotNull(); // 저장 후 ID가 생성되었는지 확인
		assertThat(savedSubCategory.getName()).isEqualTo("클래식");
		assertThat(savedSubCategory.getParentCategory()).isEqualTo(parentCategory);
	}

	@Test
	void existsByNameAndParentCategoryIsNull_whenCategoryExists_thenReturnsTrue() {
		// when
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("여행");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	void existsByNameAndParentCategoryIsNull_whenCategoryDoesNotExist_thenReturnsFalse() {
		// when
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("비존재 카테고리");

		// then
		assertThat(exists).isFalse();
	}

	@Test
	void findAllByParentCategoryIsNull_whenCalled_thenReturnsTopCategories() {
		// when
		List<Category> categories = categoryRepository.findAllByParentCategoryIsNull();

		// then
		assertThat(categories).isNotEmpty();
		assertThat(categories).extracting(Category::getName).contains("여행");
		categories.forEach(category -> assertThat(category.getParentCategory()).isNull());
	}

	@Test
	void findById_whenCategoryExists_thenReturnsCategory() {
		// when
		Optional<Category> categoryOptional = categoryRepository.findById(parentCategory.getId());

		// then
		assertThat(categoryOptional).isPresent();
		assertThat(categoryOptional.get().getName()).isEqualTo("여행");
	}

	// @Test
	// void findAllChildCategoryIds_whenCalled_thenReturnsAllChildIds() {
	// 	// when
	// 	List<Long> childCategoryIds = categoryRepository.findAllChildCategoryIds(parentCategory.getId());
	//
	// 	// then
	// 	assertThat(childCategoryIds).isNotEmpty();
	// 	assertThat(childCategoryIds).containsExactlyInAnyOrder(
	// 		parentCategory.getId(), subCategory.getId()
	// 	);
	// }
	//
	// @Test
	// void findAllChildCategoryIds_whenNoChildren_thenReturnsParentIdOnly() {
	// 	// given
	// 	Category singleParentCategory = Category.builder()
	// 		.name("독립 카테고리")
	// 		.build();
	// 	singleParentCategory = categoryRepository.save(singleParentCategory);
	//
	// 	// when
	// 	List<Long> childCategoryIds = categoryRepository.findAllChildCategoryIds(singleParentCategory.getId());
	//
	// 	// then
	// 	assertThat(childCategoryIds).containsExactly(singleParentCategory.getId());
	// }
}
