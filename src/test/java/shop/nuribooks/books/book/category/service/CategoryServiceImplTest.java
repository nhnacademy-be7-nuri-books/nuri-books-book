package shop.nuribooks.books.book.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import shop.nuribooks.books.book.category.dto.CategoryRequest;
import shop.nuribooks.books.book.category.dto.CategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.impl.CategoryServiceImpl;
import shop.nuribooks.books.exception.category.CategoryAlreadyExistException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

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

	@Test
	@DisplayName("대분류 카테고리 등록 성공 테스트")
	void testRegisterMainCategory_Success() {
		// given
		CategoryRequest request = new CategoryRequest("Electronics");
		when(categoryRepository.existsByNameAndParentCategoryIsNull(request.name())).thenReturn(false);

		Category savedCategory = Category.builder()
			.name(request.name())
			.build();
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		// when
		Category result = categoryService.registerCategory(request, null);

		// then
		assertNotNull(result);
		assertEquals(request.name(), result.getName());
		verify(categoryRepository).existsByNameAndParentCategoryIsNull(request.name());
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	@DisplayName("대분류 카테고리 중복 예외 발생 테스트")
	void testRegisterMainCategory_DuplicateException() {
		// given
		CategoryRequest request = new CategoryRequest("Electronics");
		when(categoryRepository.existsByNameAndParentCategoryIsNull(request.name())).thenReturn(true);

		// when & then
		assertThrows(CategoryAlreadyExistException.class, () -> {
			categoryService.registerCategory(request, null);
		});
		verify(categoryRepository).existsByNameAndParentCategoryIsNull(request.name());
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	@DisplayName("하위 카테고리 등록 성공 테스트")
	void testRegisterSubCategory_Success() {
		// given
		Long parentCategoryId = 1L;
		Category parentCategory = Category.builder()
			.name("Electronics")
			.build();

		CategoryRequest request = new CategoryRequest("Mobile Phones");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));

		Category savedCategory = Category.builder()
			.name(request.name())
			.parentCategory(parentCategory)
			.build();
		when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

		// when
		Category result = categoryService.registerCategory(request, parentCategoryId);

		// then
		assertNotNull(result);
		assertEquals(request.name(), result.getName());
		assertEquals(parentCategory, result.getParentCategory());
		verify(categoryRepository).findById(parentCategoryId);
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	@DisplayName("부모 카테고리 미존재 예외 발생 테스트")
	void testRegisterSubCategory_ParentNotFoundException() {
		// given
		Long parentCategoryId = 1L;
		CategoryRequest request = new CategoryRequest("Mobile Phones");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(CategoryNotFoundException.class, () -> {
			categoryService.registerCategory(request, parentCategoryId);
		});
		verify(categoryRepository).findById(parentCategoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	@DisplayName("대분류 카테고리 중복 예외 발생 테스트")
	void testRegisterCategory_DuplicateException() {
		// given
		String categoryName = "Electronics";
		CategoryRequest request = new CategoryRequest(categoryName);

		// 중복된 대분류 카테고리가 이미 존재한다고 가정
		when(categoryRepository.existsByNameAndParentCategoryIsNull(categoryName)).thenReturn(true);

		// when & then
		assertThrows(CategoryAlreadyExistException.class, () -> {
			categoryService.registerCategory(request, null);
		});

		// 리포지토리 메서드 호출 검증
		verify(categoryRepository).existsByNameAndParentCategoryIsNull(categoryName);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	/**
	 * 상위 카테고리가 없는 모든 카테고리를 조회할 때, 올바르게 응답 리스트를 반환하는지 테스트합니다.
	 */
	@Test
	void getAllCategory_whenCategoriesExist_thenReturnsCategoryList() {
		// given
		Category category1 = Category.builder().name("여행").build();
		Category category2 = Category.builder().name("문화").build();
		List<Category> categories = List.of(category1, category2);
		when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(categories);

		// when
		List<CategoryResponse> categoryResponseList = categoryService.getAllCategory();

		// then
		assertThat(categoryResponseList).hasSize(2);
		assertThat(categoryResponseList.get(0).name()).isEqualTo("여행");
		assertThat(categoryResponseList.get(1).name()).isEqualTo("문화");
		verify(categoryRepository, times(1)).findAllByParentCategoryIsNull();
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회할 때, 올바른 카테고리를 반환하는지 테스트합니다.
	 */
	@Test
	void getCategoryById_whenCategoryExists_thenReturnsCategory() {
		// given
		Long categoryId = 1L;
		Category category = Category.builder().name("여행").build();
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
			.hasMessageContaining("입력한 카테고리ID는 999 존재하지 않습니다.");
		verify(categoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void updateCategory_ShouldUpdateCategorySuccessfully() {
		// given
		Long categoryId = 1L;
		String updatedName = "Updated Category Name";
		CategoryRequest categoryRequest = new CategoryRequest(updatedName);

		Category existingCategory = Category.builder()
			.name("Old Category Name")
			.build();

		// Mock 설정
		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.existsByNameAndParentCategoryIsNull(updatedName)).thenReturn(false);

		// when
		categoryService.updateCategory(categoryRequest, categoryId);

		// then
		// save 메서드 호출 검증 제거
		// 엔티티의 필드가 업데이트되었는지 확인
		assertEquals(updatedName, existingCategory.getName());
	}

	@Test
	void updateCategory_ShouldThrowCategoryNotFoundException() {
		// Given
		Long categoryId = 1L;
		CategoryRequest dto = new CategoryRequest("Updated Category Name");

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(dto, categoryId));
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void updateCategory_ShouldThrowCategoryAlreadyExistException_WhenParentCategoryIsNull() {
		// Given
		Long categoryId = 1L;
		CategoryRequest dto = new CategoryRequest("Duplicate Category Name");
		Category existingCategory = new Category("Old Category Name", null);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.existsByNameAndParentCategoryIsNull(dto.name())).thenReturn(true);

		// When & Then
		assertThrows(CategoryAlreadyExistException.class, () -> categoryService.updateCategory(dto, categoryId));
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void updateCategory_ShouldThrowCategoryAlreadyExistException_WhenSubCategoryExists() {
		// Given
		Long categoryId = 1L;
		Category parentCategory = new Category("Parent Category", null);
		Category existingCategory = new Category("Old Category Name", parentCategory);
		CategoryRequest dto = new CategoryRequest("Duplicate SubCategory Name");
		Category duplicateSubCategory = new Category("Duplicate SubCategory Name", parentCategory);
		parentCategory.getSubCategory().add(duplicateSubCategory);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

		// When & Then
		assertThrows(CategoryAlreadyExistException.class, () -> categoryService.updateCategory(dto, categoryId));
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void deleteCategory_Success() {
		Long categoryId = 1L;
		Category category = new Category();

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		categoryService.deleteCategory(categoryId);

		verify(categoryRepository).delete(category);
	}

	@Test
	void deleteCategory_CategoryNotFound() {
		Long categoryId = 1L;

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
	}

	@Test
	void updateCategory_ShouldUpdateCategorySuccessfully_WhenParentCategoryExists() {
		// Given
		Long categoryId = 1L;
		Category parentCategory = new Category("Parent Category", null);
		Category existingCategory = new Category("Old Category Name", parentCategory);
		CategoryRequest dto = new CategoryRequest("Updated SubCategory Name");

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

		// When
		categoryService.updateCategory(dto, categoryId);

		// Then
		assertEquals("Updated SubCategory Name", existingCategory.getName());
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void updateCategory_ShouldUpdateCategorySuccessfully_WhenParentCategoryIsNull() {
		// Given
		Long categoryId = 1L;
		CategoryRequest dto = new CategoryRequest("Updated Category Name");
		Category existingCategory = new Category("Old Category Name", null);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.existsByNameAndParentCategoryIsNull(dto.name())).thenReturn(false);

		// When
		categoryService.updateCategory(dto, categoryId);

		// Then
		assertEquals("Updated Category Name", existingCategory.getName());
		verify(categoryRepository).findById(categoryId);
		verify(categoryRepository, never()).save(any(Category.class));
	}

	@Test
	void registerCategory_shouldThrowException_whenDuplicateCategoryNameExists() {
		// given
		String categoryName = "Duplicate Category";
		CategoryRequest categoryRequest = new CategoryRequest(categoryName);

		// 대분류 카테고리인 경우 중복 확인 설정
		when(categoryRepository.existsByNameAndParentCategoryIsNull(categoryName)).thenReturn(true);

		// when & then
		assertThrows(CategoryAlreadyExistException.class, () ->
			categoryService.registerCategory(categoryRequest, null)
		);
	}
}
