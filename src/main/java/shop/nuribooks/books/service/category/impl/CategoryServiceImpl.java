package shop.nuribooks.books.service.category.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.entity.book.category.Category;
import shop.nuribooks.books.exception.category.CategoryAlreadyExistException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;
import shop.nuribooks.books.repository.category.CategoryRepository;
import shop.nuribooks.books.service.category.CategoryService;

/**
 * 카테고리 관련 작업을 처리하는 서비스 구현체.
 */
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
	 * 새로운 대분류 카테고리를 등록합니다.
	 *
	 * @param dto 카테고리 등록 요청 DTO
	 * @return 등록된 카테고리 엔티티
	 * @throws CategoryAlreadyExistException 동일한 이름의 대분류 카테고리가 이미 존재하는 경우
	 */
	@Override
	@Transactional
	public Category registerMainCategory(CategoryRequest dto) {
		boolean isDuplicate = categoryRepository.existsByNameAndParentCategoryIsNull(dto.name());
		if (isDuplicate) {
			throw new CategoryAlreadyExistException(
				"Top-level category with name '" + dto.name() + "' already exists.");
		}
		Category category = Category.builder()
			.name(dto.name())
			.level(0)
			.build();
		return categoryRepository.save(category);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류 카테고리를 등록합니다.
	 *
	 * @param dto 하위 분류 등록 요청 DTO
	 * @param parentCategoryId 부모 카테고리의 ID
	 * @return 등록된 하위 카테고리 엔티티
	 * @throws CategoryNotFoundException 부모 카테고리를 찾을 수 없는 경우
	 * @throws CategoryAlreadyExistException 동일한 이름의 하위 카테고리가 이미 존재하는 경우
	 */
	@Override
	@Transactional
	public Category registerSubCategory(CategoryRequest dto, Long parentCategoryId) {
		Category parentCategory = categoryRepository.findById(parentCategoryId)
			.orElseThrow(() -> new CategoryNotFoundException(
				"Parent category not found with ID: " + parentCategoryId));

		boolean isDuplicate = parentCategory.getSubCategory().stream()
			.anyMatch(subCategory -> subCategory.getName().equals(dto.name()));

		if (isDuplicate) {
			throw new CategoryAlreadyExistException(
				"Category with name '" + dto.name() + "' already exists under parent category with ID: "
					+ parentCategoryId);
		}

		Category category = Category.builder()
			.name(dto.name())
			.level(parentCategory.getLevel() + 1)
			.parentCategory(parentCategory)
			.build();
		return categoryRepository.save(category);
	}
}
