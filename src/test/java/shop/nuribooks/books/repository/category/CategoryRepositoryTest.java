package shop.nuribooks.books.repository.category;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

/**
 * CategoryRepository의 기능을 테스트하는 클래스.
 */
@DataJpaTest
class CategoryRepositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;

	/**
	 * 카테고리 이름이 존재하며 부모 카테고리가 없는 경우, true를 반환하는지 테스트합니다.
	 */
	@Sql("/repository/category/category-sample.sql")
	@Test
	void existsByNameAndParentCategoriesIsNull() {
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("여행");
		assertThat(exists).isTrue();
	}

	/**
	 * 존재하지 않는 카테고리 이름에 대해 false를 반환하는지 테스트합니다.
	 */
	@Sql("/repository/category/category-sample.sql")
	@Test
	void checkIfCategoryDoesNotExist() {
		boolean exists = categoryRepository.existsByNameAndParentCategoryIsNull("비존재 카테고리");
		assertThat(exists).isFalse();
	}

}

