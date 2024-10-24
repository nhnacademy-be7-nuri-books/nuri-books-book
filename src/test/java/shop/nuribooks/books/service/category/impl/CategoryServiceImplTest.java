package shop.nuribooks.books.service.category.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.dto.category.CategoryResponse;
import shop.nuribooks.books.entity.book.Category;
import shop.nuribooks.books.exception.category.CategoryAlreadyExistException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;
import shop.nuribooks.books.repository.category.CategoryRepository;

/**
 * CategoryServiceImpl의 기능을 테스트하는 클래스.
 */
class CategoryServiceImplTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryServiceImpl categoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * 대분류 카테고리가 존재하지 않을 때, 카테고리가 성공적으로 저장되는지 테스트합니다.
	 */
	@Test
	void registerMainCategory_whenCategoryDoesNotExist_thenCategoryIsSaved() {
		// given
		CategoryRequest dto = new CategoryRequest("여행");
		when(categoryRepository.existsByNameAndParentCategoryIsNull(dto.name())).thenReturn(false);
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		Category category = categoryService.registerMainCategory(dto);

		// then
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo("여행");
		assertThat(category.getLevel()).isEqualTo(0);
		verify(categoryRepository, times(1)).save(any(Category.class));
	}

	/**
	 * 대분류 카테고리가 이미 존재할 때, 예외가 발생하는지 테스트합니다.
	 */
	@Test
	void registerMainCategory_whenCategoryExists_thenThrowsException() {
		// given
		CategoryRequest dto = new CategoryRequest("여행");
		when(categoryRepository.existsByNameAndParentCategoryIsNull(dto.name())).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> categoryService.registerMainCategory(dto))
			.isInstanceOf(CategoryAlreadyExistException.class)
			.hasMessageContaining("Top-level category with name '여행' already exists.");
	}

	/**
	 * 부모 카테고리가 존재할 때, 하위 분류 카테고리가 성공적으로 저장되는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenParentCategoryExists_thenSubCategoryIsSaved() {
		// given
		Long parentCategoryId = 1L;
		Category parentCategory = Category.builder().name("여행").level(0).build();
		CategoryRequest dto = new CategoryRequest("국내 여행");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		Category subCategory = categoryService.registerSubCategory(dto, parentCategoryId);

		// then
		assertThat(subCategory).isNotNull();
		assertThat(subCategory.getName()).isEqualTo("국내 여행");
		assertThat(subCategory.getLevel()).isEqualTo(1);
		assertThat(subCategory.getParentCategory()).isEqualTo(parentCategory);
		verify(categoryRepository, times(1)).save(any(Category.class));
	}

	/**
	 * 부모 카테고리를 찾을 수 없을 때, 예외가 발생하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenParentCategoryDoesNotExist_thenThrowsException() {
		// given
		Long parentCategoryId = 1L;
		CategoryRequest dto = new CategoryRequest("국내 여행");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> categoryService.registerSubCategory(dto, parentCategoryId))
			.isInstanceOf(CategoryNotFoundException.class)
			.hasMessageContaining("Parent category not found with ID: " + parentCategoryId);
	}

	/**
	 * 동일한 이름의 하위 카테고리가 이미 존재할 때, 예외가 발생하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenSubCategoryExists_thenThrowsException() {
		// given
		Long parentCategoryId = 1L;
		Category parentCategory = Category.builder().name("여행").level(0).build();
		Category subCategory = Category.builder().name("국내 여행").level(1).parentCategory(parentCategory).build();
		parentCategory.getSubCategory().add(subCategory);
		CategoryRequest dto = new CategoryRequest("국내 여행");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));

		// when & then
		assertThatThrownBy(() -> categoryService.registerSubCategory(dto, parentCategoryId))
			.isInstanceOf(CategoryAlreadyExistException.class)
			.hasMessageContaining(
				"Category with name '국내 여행' already exists under parent category with ID: " + parentCategoryId);
	}

	/**
	 * 상위 카테고리가 없는 모든 카테고리를 조회할 때, 올바르게 응답 리스트를 반환하는지 테스트합니다.
	 */
	@Test
	void getAllCategory_whenCategoriesExist_thenReturnsCategoryList() {
		// given
		Category category1 = Category.builder().name("여행").level(0).build();
		Category category2 = Category.builder().name("문화").level(0).build();
		List<Category> categories = List.of(category1, category2);
		when(categoryRepository.findAllTopCategoriesWithChildren()).thenReturn(categories);

		// when
		List<CategoryResponse> categoryResponseList = categoryService.getAllCategory();

		// then
		assertThat(categoryResponseList).hasSize(2);
		assertThat(categoryResponseList.get(0).name()).isEqualTo("여행");
		assertThat(categoryResponseList.get(1).name()).isEqualTo("문화");
		verify(categoryRepository, times(1)).findAllTopCategoriesWithChildren();
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회할 때, 올바른 카테고리를 반환하는지 테스트합니다.
	 */
	@Test
	void getCategoryById_whenCategoryExists_thenReturnsCategory() {
		// given
		Long categoryId = 1L;
		Category category = Category.builder().name("여행").level(0).build();
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		// when
		CategoryResponse categoryResponse = categoryService.getCategoryById(categoryId);

		// then
		assertThat(categoryResponse).isNotNull();
		assertThat(categoryResponse.name()).isEqualTo("여행");
		verify(categoryRepository, times(1)).findById(categoryId);
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 찾을 수 없을 때, 예외가 발생하는지 테스트합니다.
	 */
	@Test
	void getCategoryById_whenCategoryDoesNotExist_thenThrowsException() {
		// given
		Long categoryId = 999L;
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> categoryService.getCategoryById(categoryId))
			.isInstanceOf(CategoryNotFoundException.class)
			.hasMessageContaining("category not found with ID: " + categoryId);
		verify(categoryRepository, times(1)).findById(categoryId);
	}

}