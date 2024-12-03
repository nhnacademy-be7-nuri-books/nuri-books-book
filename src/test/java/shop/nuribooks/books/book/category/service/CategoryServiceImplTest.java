package shop.nuribooks.books.book.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
			.hasMessageContaining("카테고리 이름 '여행' 가 이미 존재합니다");
	}

	/**
	 * 부모 카테고리가 존재할 때, 하위 분류 카테고리가 성공적으로 저장되는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenParentCategoryExists_thenSubCategoryIsSaved() {
		// given
		Long parentCategoryId = 1L;
		Category parentCategory = Category.builder().name("여행").build();
		CategoryRequest dto = new CategoryRequest("국내 여행");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		Category subCategory = categoryService.registerSubCategory(dto, parentCategoryId);

		// then
		assertThat(subCategory).isNotNull();
		assertThat(subCategory.getName()).isEqualTo("국내 여행");
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
			.hasMessageContaining("력한 카테고리ID는 1 존재하지 않습니다.");
	}

	/**
	 * 동일한 이름의 하위 카테고리가 이미 존재할 때, 예외가 발생하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenSubCategoryExists_thenThrowsException() {
		// given
		Long parentCategoryId = 1L;
		Category parentCategory = Category.builder().name("여행").build();
		Category subCategory = Category.builder().name("국내 여행").parentCategory(parentCategory).build();
		parentCategory.getSubCategory().add(subCategory);
		CategoryRequest dto = new CategoryRequest("국내 여행");
		when(categoryRepository.findById(parentCategoryId)).thenReturn(Optional.of(parentCategory));

		// when & then
		assertThatThrownBy(() -> categoryService.registerSubCategory(dto, parentCategoryId))
			.isInstanceOf(CategoryAlreadyExistException.class)
			.hasMessageContaining("카테고리 이름 '국내 여행' 가 이미 존재합니다");
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
		Long categoryId = 1L;

		String updatedName = "Updated Category Name";
		CategoryRequest categoryRequest = new CategoryRequest(updatedName);

		Category existingCategory = Category.builder()
			.name("Old Category Name")
			.build();

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
		when(categoryRepository.existsByNameAndParentCategoryIsNull(updatedName)).thenReturn(false);
		// when
		categoryService.updateCategory(categoryRequest, categoryId);
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
	void getAllCategoryTree_WhenCategoriesExist_ShouldReturnCategoryTree() {
		// Given
		Category rootCategory1 = new Category("Root Category 1", null);
		Category rootCategory2 = new Category("Root Category 2", null);

		Category subCategory1 = new Category("SubCategory 1", rootCategory1);
		rootCategory1.getSubCategory().add(subCategory1);

		when(categoryRepository.findAllByParentCategoryIsNull()).thenReturn(List.of(rootCategory1, rootCategory2));

		// When
		List<CategoryResponse> categoryTree = categoryService.getAllCategoryTree();

		// Then
		assertThat(categoryTree).hasSize(2);
		assertThat(categoryTree.getFirst().name()).isEqualTo("Root Category 1");
		assertThat(categoryTree.getFirst().subCategories()).hasSize(1);

		verify(categoryRepository, times(1)).findAllByParentCategoryIsNull();
	}

	@Test
	void buildCategoryTree_ShouldReturnCategoryTree() {
		// Given
		Category rootCategory = new Category("Root Category", null);
		Category subCategory1 = new Category("SubCategory 1", rootCategory);
		Category subCategory2 = new Category("SubCategory 2", rootCategory);
		rootCategory.getSubCategory().addAll(List.of(subCategory1, subCategory2));

		// When
		CategoryResponse categoryTree = categoryService.buildCategoryTree(rootCategory);

		// Then
		assertThat(categoryTree.name()).isEqualTo("Root Category");
		assertThat(categoryTree.subCategories()).hasSize(2);
		assertThat(categoryTree.subCategories().getFirst().name()).isEqualTo("SubCategory 1");
	}

	@Test
	void getCategoryNameById_ShouldReturnCategoryName() {
		// Given
		Long categoryId = 1L;
		Category category = new Category("Category Name", null);

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

		// When
		CategoryRequest categoryName = categoryService.getCategoryNameById(categoryId);

		// Then
		assertThat(categoryName.name()).isEqualTo("Category Name");
		verify(categoryRepository, times(1)).findById(categoryId);
	}

	@Test
	void getCategoryNameById_ShouldThrowException_WhenCategoryNotFound() {
		// Given
		Long categoryId = 1L;

		when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> categoryService.getCategoryNameById(categoryId))
			.isInstanceOf(CategoryNotFoundException.class)
			.hasMessageContaining("입력한 카테고리ID는 " + categoryId + " 존재하지 않습니다.");
		verify(categoryRepository, times(1)).findById(categoryId);
	}

}
