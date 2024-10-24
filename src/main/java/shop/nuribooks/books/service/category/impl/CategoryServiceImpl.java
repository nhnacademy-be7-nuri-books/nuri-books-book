package shop.nuribooks.books.service.category.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.dto.category.CategoryResponse;
import shop.nuribooks.books.entity.book.Category;
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
				"카테고리 이름 '" + dto.name() + "'가 이미 존재합니다.");
		}
		Category category = Category.builder()
			.name(dto.name())
			.level(0)
			.build();
		return categoryRepository.save(category);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류 카테고리를 등록합니다.
	 * @author janghyun
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
			.orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));

		boolean isDuplicate = parentCategory.getSubCategory().stream()
			.anyMatch(subCategory -> subCategory.getName().equals(dto.name()));

		if (isDuplicate) {
			throw new CategoryAlreadyExistException(
				"카테고리 이름 '" + dto.name() + "' 가 이미 존재합니다");
		}

		Category category = Category.builder()
			.name(dto.name())
			.level(parentCategory.getLevel() + 1)
			.parentCategory(parentCategory)
			.build();
		return categoryRepository.save(category);
	}

	/**
	 * 상위 카테고리가 없는 모든 카테고리를 조회하여 반환합니다.
	 * @author janghyun
	 * @return 상위 카테고리가 없는 모든 카테고리의 응답 리스트
	 */
	@Override
	public List<CategoryResponse> getAllCategory() {
		List<Category> categoryList = categoryRepository.findAllByParentCategoryIsNull();
		List<CategoryResponse> categoryResponseList = new ArrayList<>();
		for (Category category : categoryList) {
			categoryResponseList.add(new CategoryResponse(category));
		}
		return categoryResponseList;
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회하여 반환합니다.
	 * 카테고리가 존재하지 않을 경우 CategoryNotFoundException을 발생시킵니다.
	 *
	 * @author janghyun
	 * @param categoryId 조회할 카테고리의 ID
	 * @return 조회된 카테고리의 응답 객체
	 * @throws CategoryNotFoundException 주어진 ID에 해당하는 카테고리가 존재하지 않을 경우
	 */
	@Override
	public CategoryResponse getCategoryById(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		return new CategoryResponse(category);
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 업데이트합니다.
	 * 카테고리가 존재하지 않을 경우 CategoryNotFoundException을 발생시킵니다.
	 *
	 * @author janghyun
	 * @param dto 업데이트할 카테고리의 정보가 담긴 객체
	 * @param categoryId 업데이트할 카테고리의 ID
	 * @return 업데이트된 카테고리의 응답 객체
	 * @throws CategoryNotFoundException 주어진 ID에 해당하는 카테고리가 존재하지 않을 경우
	 */
	@Override
	public CategoryResponse updateCategory(CategoryRequest dto, Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		category.setName(dto.name());

		return new CategoryResponse(categoryRepository.save(category));
	}

}
