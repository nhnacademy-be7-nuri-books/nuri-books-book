package shop.nuribooks.books.book.category.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import shop.nuribooks.books.book.category.entity.Category;

/**
 * 브레드크럼 항목을 표현하는 DTO.
 *
 * @param id   카테고리 ID
 * @param name 카테고리 이름
 */
@Builder
public record SimpleCategoryResponse(Long id, String name) {
	/**
	 * 브레드크럼을 구성하기 위한 경로 리스트를 빌드합니다.
	 *
	 * @param category 카테고리 엔티티
	 * @return 브레드크럼 경로 리스트
	 */
	public static List<SimpleCategoryResponse> buildBreadcrumbs(Category category) {
		List<SimpleCategoryResponse> breadcrumbs = new ArrayList<>();
		Category current = category;
		while (current != null) {
			breadcrumbs.add(0, SimpleCategoryResponse.builder()
				.id(current.getId())
				.name(current.getName())
				.build());
			current = current.getParentCategory();
		}
		return breadcrumbs;
	}
}