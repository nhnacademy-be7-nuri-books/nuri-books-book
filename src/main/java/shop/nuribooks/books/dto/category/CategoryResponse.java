package shop.nuribooks.books.dto.category;

import java.util.List;
import java.util.stream.Collectors;

import shop.nuribooks.books.entity.book.Category;

/**
 * 카테고리 등록 응답을 나타내는 DTO 클래스.
 */
public record CategoryResponse(Long id, String name, String parentCategoryName, Integer level,
                               List<CategoryResponse> children) {

	/**
	 * Category 엔티티로부터 CategoryRegisterRes 객체를 생성하는 생성자.
	 *
	 * @param entity Category 엔티티 객체
	 */
	public CategoryResponse(Category entity) {
		this(entity.getId(),
			entity.getName(),
			(entity.getParentCategory() == null) ? "대분류" : entity.getParentCategory().getName(),
			entity.getLevel(),
			(entity.getSubCategory() == null) ? null :
				entity.getSubCategory().stream()
					.map(CategoryResponse::new)
					.collect(Collectors.toList()));
	}
}
