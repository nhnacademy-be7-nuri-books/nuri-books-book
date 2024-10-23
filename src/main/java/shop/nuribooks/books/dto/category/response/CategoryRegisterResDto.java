package shop.nuribooks.books.dto.category.response;

import java.util.Map;
import java.util.stream.Collectors;

import shop.nuribooks.books.dto.category.CategoriesDto;
import shop.nuribooks.books.entity.book.Category;

public record CategoryRegisterResDto(Long id,
									 String name,
									 String parentCategoryName,
									 Integer level,
									 Map<String, CategoriesDto> children) {
	public CategoryRegisterResDto(Category entity) {
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
