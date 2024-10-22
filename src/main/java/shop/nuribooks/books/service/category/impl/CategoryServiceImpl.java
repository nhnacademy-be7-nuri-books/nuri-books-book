package shop.nuribooks.books.service.category.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.nuribooks.books.dto.category.request.CategoryRegisterReqDto;
import shop.nuribooks.books.entity.Category;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.repository.CategoryRepository;
import shop.nuribooks.books.service.category.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional
	public Category registerCategory(CategoryRegisterReqDto dto) {
		Category parentCategory = null;

		int level = 0;

		if (dto.parentCategoryId() != null) {
			parentCategory = categoryRepository.findById(dto.parentCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException(
					"Parent category not found with ID: " + dto.parentCategoryId()));

			level = parentCategory.getLevel() + 1;

			// 부모 카테고리의 하위에 같은 이름의 카테고리가 있는지 확인
			boolean isDuplicate = parentCategory.getSubCategory().stream()
				.anyMatch(subCategory -> subCategory.getName().equals(dto.name()));

			if (isDuplicate) {
				throw new IllegalArgumentException(
					"Category with name '" + dto.name() + "' already exists under parent category with ID: "
						+ dto.parentCategoryId());
			}
		} else {
			// 대분류에서 같은 이름의 카테고리가 있는지 확인
			boolean isDuplicate = categoryRepository.existsByNameAndParentCategoryIsNull(dto.name());
			if (isDuplicate) {
				throw new IllegalArgumentException("Top-level category with name '" + dto.name() + "' already exists.");
			}
		}

		Category category = Category.builder()
			.name(dto.name())
			.level(level)
			.parentCategory(parentCategory)
			.build();

		return categoryRepository.save(category);
	}
}
