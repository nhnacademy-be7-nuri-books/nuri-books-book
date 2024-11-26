package shop.nuribooks.books.book.category.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.book.category.dto.CategoryRequest;
import shop.nuribooks.books.book.category.dto.CategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.entity.CategoryEditor;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.CategoryService;
import shop.nuribooks.books.exception.category.CategoryAlreadyExistException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

/**
 * 카테고리 관련 작업을 처리하는 서비스 구현체.
 *
 * @author janghyun
 **/
@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * CategoryServiceImpl의 생성자.
	 *
	 * @param categoryRepository 카테고리 레포지토리
	 */
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	/**
	 * getCategoryEditor : 카테고리 편집 빌더
	 *
	 * @param categoryRequest 요청된 태그 정보 담긴 객체
	 * @param category 기존 태그 정보를 담은 객체
	 * @return 수정된 정보를 포함한 객체
	 */
	private static CategoryEditor getCategoryEditor(CategoryRequest categoryRequest, Category category) {
		CategoryEditor.CategoryEditorBuilder builder = category.toEditor();
		return builder
			.name(categoryRequest.name())
			.build();
	}

	/**
	 * 새로운 대분류 카테고리를 등록합니다.
	 *
	 * @param categoryRequest 카테고리 등록 요청 DTO
	 * @return 등록된 카테고리 엔티티
	 * @throws CategoryAlreadyExistException 동일한 이름의 대분류 카테고리가 이미 존재하는 경우
	 */
	@Override
	@Transactional
	public Category registerMainCategory(CategoryRequest categoryRequest) {
		boolean isDuplicate = categoryRepository.existsByNameAndParentCategoryIsNull(categoryRequest.name());
		if (isDuplicate) {
			throw new CategoryAlreadyExistException(categoryRequest.name());
		}
		Category category = Category.builder()
			.name(categoryRequest.name())
			.build();
		return categoryRepository.save(category);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류 카테고리를 등록합니다.
	 *
	 * @param categoryRequest 하위 분류 등록 요청 DTO
	 * @param parentCategoryId 부모 카테고리의 ID
	 * @return 등록된 하위 카테고리 엔티티
	 * @throws CategoryNotFoundException 부모 카테고리를 찾을 수 없는 경우
	 * @throws CategoryAlreadyExistException 동일한 이름의 하위 카테고리가 이미 존재하는 경우
	 */
	@Override
	@Transactional
	public Category registerSubCategory(CategoryRequest categoryRequest, Long parentCategoryId) {
		Category parentCategory = categoryRepository.findById(parentCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));

		boolean isDuplicate = parentCategory.getSubCategory().stream()
			.anyMatch(subCategory -> subCategory.getName().equals(categoryRequest.name()));

		if (isDuplicate) {
			throw new CategoryAlreadyExistException(categoryRequest.name());
		}

		Category category = Category.builder()
			.name(categoryRequest.name())
			.parentCategory(parentCategory)
			.build();
		return categoryRepository.save(category);
	}

	/**
	 * 상위 카테고리가 없는 모든 카테고리를 조회하여 반환합니다.
	 *
	 * @return 상위 카테고리가 없는 모든 카테고리의 응답 리스트
	 */
	@Override
	public List<CategoryResponse> getAllCategory() {
		List<Category> categoryList = categoryRepository.findAllByParentCategoryIsNull();
		List<CategoryResponse> categoryResponseList = new ArrayList<>();
		for (Category category : categoryList) {
			categoryResponseList.add(CategoryResponse.from(category));
		}
		return categoryResponseList;
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회하여 반환합니다.
	 * 카테고리가 존재하지 않을 경우 CategoryNotFoundException을 발생시킵니다.
	 *
	 * @param categoryId 조회할 카테고리의 ID
	 * @return 조회된 카테고리의 응답 객체
	 * @throws CategoryNotFoundException 주어진 ID에 해당하는 카테고리가 존재하지 않을 경우
	 */
	@Override
	public CategoryResponse getCategoryById(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		return CategoryResponse.from(category);
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 업데이트합니다.
	 * 카테고리가 존재하지 않을 경우 CategoryNotFoundException을 발생시킵니다.
	 *
	 * @param categoryRequest 업데이트할 카테고리의 정보가 담긴 객체
	 * @param categoryId 업데이트할 카테고리의 ID
	 * @return 업데이트된 카테고리의 응답 객체
	 * @throws CategoryNotFoundException 주어진 ID에 해당하는 카테고리가 존재하지 않을 경우
	 */
	@Override
	@Transactional
	public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		Category parentCategory = category.getParentCategory();

		if (parentCategory == null) {
			if (categoryRepository.existsByNameAndParentCategoryIsNull(categoryRequest.name())) {
				throw new CategoryAlreadyExistException(categoryRequest.name());
			}
		} else {
			if (parentCategory.getSubCategory().stream()
				.anyMatch(subCategory -> subCategory.getName().equals(categoryRequest.name()))) {
				throw new CategoryAlreadyExistException(categoryRequest.name());
			}
		}

		CategoryEditor categoryEditor = getCategoryEditor(categoryRequest, category);

		category.edit(categoryEditor);

		return CategoryResponse.from(category);
	}

	/**
	 * 특정 카테고리를 삭제합니다.
	 *
	 * @param categoryId 삭제할 카테고리의 ID
	 */
	@Transactional
	@Override
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);
		categoryRepository.delete(category);
	}

	/**
	 * 최상위 카테고리들을 가져와 buildCategoryTree를 호출하여 전체 트리구조 생성
	 * @return 트리구조의 CategoryResponse 리스트 반환
	 */
	@Override
	public List<CategoryResponse> getAllCategoryTree() {
		List<Category> rootCategories = categoryRepository.findAllByParentCategoryIsNull();
		List<CategoryResponse> categoryTree = new ArrayList<>();
		for (Category rootCategory : rootCategories) {
			categoryTree.add(buildCategoryTree(rootCategory));
		}
		return categoryTree;
	}

	/**
	 * @param category 최상위 카테고리
	 * @return 각 카테고리의 하위카테고리들을 추가한 CategoryResponse
	 */
	@Override
	public CategoryResponse buildCategoryTree(Category category) {
		List<CategoryResponse> subCategories = new ArrayList<>();
		for (Category subCategory : category.getSubCategory()) {
			subCategories.add(buildCategoryTree(subCategory));
		}
		return new CategoryResponse(category.getId(), category.getName(), subCategories);
	}

	@Override
	public CategoryRequest getCategoryNameById(Long categoryId) {
		String categoryName = categoryRepository.findById(categoryId)
			.map(Category::getName)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		return new CategoryRequest(categoryName);
	}
}

