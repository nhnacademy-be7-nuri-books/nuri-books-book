package shop.nuribooks.books.dto.category.response;

import java.util.Map;
import java.util.stream.Collectors;

import shop.nuribooks.books.entity.book.category.Category;

/**
 * 카테고리 등록 응답을 나타내는 DTO 클래스.
 */
public record CategoryRegisterRes(Long id, String name, String parentCategoryName, Integer level,
								  Map<String, CategoryRegisterRes> children) {

	/**
	 * Category 엔티티로부터 CategoryRegisterRes 객체를 생성하는 생성자.
	 *
	 * @param entity Category 엔티티 객체
	 */
	public CategoryRegisterRes(Category entity) {
		this(entity.getId(),
			entity.getName(),
			(entity.getParentCategory() == null) ? "대분류" : entity.getParentCategory().getName(),
			entity.getLevel(),
			(entity.getSubCategory() == null) ? null :
				entity.getSubCategory().stream()
					.collect(Collectors.toMap(
						Category::getName, CategoryRegisterRes::new)));
	}
}
