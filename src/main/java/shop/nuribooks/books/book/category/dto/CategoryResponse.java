package shop.nuribooks.books.book.category.dto;

import java.util.List;
import java.util.stream.Collectors;

import shop.nuribooks.books.book.category.entitiy.Category;

/**
 * 카테고리 응답 DTO.
 *
 * @param id            카테고리 ID
 * @param name          카테고리 이름
 * @param parent        부모 카테고리 (부모가 없을 경우 null)
 * @param subCategories 하위 카테고리 리스트
 */
public record CategoryResponse(
	Long id,
	String name,
	ParentCategory parent,
	List<CategoryResponse> subCategories) {

	/**
	 * 부모 카테고리를 표현하는 DTO.
	 *
	 * @param id   부모 카테고리 ID
	 * @param name 부모 카테고리 이름
	 */
	public record ParentCategory(Long id, String name) {
	}

	/**
	 * Category 엔티티를 기반으로 응답 DTO를 생성합니다.
	 * 모든 하위 카테고리를 포함하여 변환합니다.
	 *
	 * @param category 카테고리 엔티티
	 * @return 카테고리 응답 DTO
	 */
	public static CategoryResponse fromAll(Category category) {
		List<CategoryResponse> subCategoryResponses = category.getSubCategory().stream()
			.map(CategoryResponse::fromAll)
			.collect(Collectors.toList());

		ParentCategory parent = category.getParentCategory() != null
			? new ParentCategory(category.getParentCategory().getId(), category.getParentCategory().getName())
			: null;

		return new CategoryResponse(
			category.getId(),
			category.getName(),
			parent,
			subCategoryResponses
		);
	}

	/**
	 * Category 엔티티를 기반으로 응답 DTO를 생성합니다.
	 * 바로 하위 카테고리들만 포함하여 변환합니다.
	 *
	 * @param category 카테고리 엔티티
	 * @return 카테고리 응답 DTO
	 */
	public static CategoryResponse fromOneLevel(Category category) {
		// 한 단계 하위 카테고리만 포함
		List<CategoryResponse> subCategoryResponses = category.getSubCategory().stream()
			.map(subCategory -> new CategoryResponse(subCategory.getId(),
				subCategory.getName(),
				null,
				List.of()))
			.collect(Collectors.toList());

		ParentCategory parent = category.getParentCategory() != null
			? new ParentCategory(category.getParentCategory().getId(), category.getParentCategory().getName())
			: null;

		return new CategoryResponse(
			category.getId(),
			category.getName(),
			parent,
			subCategoryResponses
		);
	}

}

