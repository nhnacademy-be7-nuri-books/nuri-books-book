package shop.nuribooks.books.service.category;

import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.entity.book.Category;

/**
 * 카테고리 관련 서비스 인터페이스.
 */
public interface CategoryService {
	/**
	 * 새로운 대분류 카테고리를 등록합니다.
	 *
	 * @param dto 카테고리 등록 요청 DTO
	 * @return 등록된 카테고리 엔티티
	 */
	Category registerMainCategory(CategoryRequest dto);

	/**
	 * 기존 대분류 아래에 새로운 하위 분류 카테고리를 등록합니다.
	 *
	 * @param dto 하위 분류 등록 요청 DTO
	 * @param parentCategoryId 부모 카테고리의 ID
	 * @return 등록된 하위 카테고리 엔티티
	 */
	Category registerSubCategory(CategoryRequest dto, Long parentCategoryId);
}
