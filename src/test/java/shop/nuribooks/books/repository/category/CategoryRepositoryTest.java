package shop.nuribooks.books.repository.category;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.entity.book.Category;

/**
 * CategoryRepository의 기능을 테스트하는 클래스.
 */
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	@Order(1)
	@Transactional
	@Rollback(false)
	void saveCategory() {
		// given
		Category category = Category.builder()
			.name("여행")
			.level(0)
			.build();

		// when
		Category savedCategory = categoryRepository.save(category);

		// then
		assertThat(savedCategory).isNotNull();
		assertThat(savedCategory.getId()).isEqualTo(1L);
		assertThat(savedCategory.getId()).isNotNull(); // 저장 후 ID가 생성되었는지 확인
		assertThat(savedCategory.getName()).isEqualTo("여행");
		assertThat(savedCategory.getLevel()).isEqualTo(0);
	}

	@Test
	@Order(2)
	void saveSubCategory() {
		Category parentCategory = categoryRepository.findById(1L)
			.orElseThrow(() -> new IllegalStateException("부모 카테고리를 찾을 수 없습니다."));

		Category subCategory = Category.builder()
			.name("국내 여행")
			.level(1)
			.parentCategory(parentCategory)
			.build();

		// when
		Category savedSubCategory = categoryRepository.save(subCategory);

		// then
		assertThat(savedSubCategory).isNotNull();
		assertThat(savedSubCategory.getId()).isNotNull(); // 저장 후 ID가 생성되었는지 확인
		assertThat(savedSubCategory.getName()).isEqualTo("국내 여행");
		assertThat(savedSubCategory.getLevel()).isEqualTo(1);
		assertThat(savedSubCategory.getParentCategory()).isEqualTo(parentCategory);
	}

	/**
	 * 카테고리 이름이 존재하며 부모 카테고리가 없는 경우, true를 반환하는지 테스트합니다.
	 */
	@Test
	@Order(3)
	void existsByNameAndParentCategoriesIsNull() {
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("여행");
		assertThat(exists).isTrue();
	}

	/**
	 * 존재하지 않는 카테고리 이름에 대해 false를 반환하는지 테스트합니다.
	 */
	@Test
	void checkIfCategoryDoesNotExist() {
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("비존재 카테고리");
		assertThat(exists).isFalse();
	}

	/**
	 * 부모 카테고리가 없는 모든 카테고리를 조회할 때 올바른 결과를 반환하는지 테스트합니다.
	 */
	@Test
	void findAllByParentCategoryIsNull_whenCalled_thenReturnsTopCategories() {
		// when
		List<Category> categories = categoryRepository.findAllByParentCategoryIsNull();

		// then
		assertThat(categories).isNotEmpty();
		assertThat(categories.size()).isGreaterThanOrEqualTo(1);
		assertThat(categories.get(0).getParentCategory()).isNull();
	}

	/**
	 * 데이터베이스에 해당하는 카테고리가 없는 경우 올바르게 false를 반환하는지 테스트합니다.
	 */
	@Test
	void existsByNameAndParentCategoryIsNull_whenCategoryNotExists_thenReturnsFalse() {
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("비존재하는 카테고리");
		assertThat(exists).isFalse();
	}

	/**
	 * 특정 ID로 카테고리를 조회할 때 올바른 카테고리를 반환하는지 테스트합니다.
	 */
	@Test
	void findById_whenCategoryExists_thenReturnsCategory() {
		// given
		Long categoryId = 1L; // category-sample.sql에 존재하는 ID를 사용

		// when
		Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

		// then
		assertThat(categoryOptional).isPresent();
		assertThat(categoryOptional.get().getName()).isEqualTo("여행"); // 예제 데이터의 이름과 일치하는지 확인
	}
}

