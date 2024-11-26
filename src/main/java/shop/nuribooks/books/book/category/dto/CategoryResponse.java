package shop.nuribooks.books.book.category.dto;

import java.util.Collections;
import java.util.List;

import shop.nuribooks.books.book.category.entity.Category;

/**
 * 카테고리 등록 응답을 나타내는 DTO 클래스.
 */
public record CategoryResponse(Long id,
                               String name,
                               List<CategoryResponse> subCategories) {

	/**
	 * Category 엔티티를 기반으로 응답 DTO를 생성합니다.
	 * 모든 하위 카테고리를 포함하여 변환합니다.
	 *
	 * @param category 카테고리 엔티티
	 * @return 카테고리 응답 DTO
	 */
	public static CategoryResponse from(Category category) {
		List<CategoryResponse> subCategoryResponses = Collections.emptyList();
		if (category.getSubCategory() != null && !category.getSubCategory().isEmpty()) {
			subCategoryResponses = category.getSubCategory().stream()
				.map(CategoryResponse::from)
				.toList();
		}

		return new CategoryResponse(
			category.getId(),
			category.getName(),
			subCategoryResponses
			// category.getParentCategory() != null ? category.getParentCategory().getId() : null
		);
	}
}
