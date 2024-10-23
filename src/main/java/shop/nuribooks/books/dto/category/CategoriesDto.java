package shop.nuribooks.books.dto.category;

import java.util.Map;
import java.util.stream.Collectors;

import shop.nuribooks.books.entity.book.Category;

public record CategoriesDto(Long id,
							String name,
							String parentCategoryName,
							Integer level,
							Map<String, CategoriesDto> children) {
	public CategoriesDto(Category entity) {
		this(entity.getId(),
			entity.getName(),
			(entity.getParentCategory() == null) ? "대분류" : entity.getParentCategory().getName(),
			entity.getLevel(),
			(entity.getSubCategory() == null) ? null :
				entity.getSubCategory().stream()
					.collect(Collectors.toMap(
						Category::getName, CategoriesDto::new)));
	}
}
