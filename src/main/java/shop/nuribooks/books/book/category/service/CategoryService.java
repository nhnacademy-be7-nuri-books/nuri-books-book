package shop.nuribooks.books.book.category.service;

import java.util.List;

import shop.nuribooks.books.book.category.dto.CategoryRequest;
import shop.nuribooks.books.book.category.dto.CategoryResponse;
import shop.nuribooks.books.book.category.entity.Category;

/**
 * 카테고리 관련 서비스 인터페이스.
 */
public interface CategoryService {
	/**
	 * 새로운 대분류 카테고리를 등록합니다.
	 * @author janghyun
	 * @param dto 카테고리 등록 요청 DTO
	 * @return 등록된 카테고리 엔티티
	 */
	Category registerMainCategory(CategoryRequest dto);

	/**
	 * 기존 대분류 아래에 새로운 하위 분류 카테고리를 등록합니다.
	 * @author janghyun
	 * @param dto 하위 분류 등록 요청 DTO
	 * @param parentCategoryId 부모 카테고리의 ID
	 * @return 등록된 하위 카테고리 엔티티
	 */
	Category registerSubCategory(CategoryRequest dto, Long parentCategoryId);

	/**
	 * 모든 카테고리 목록을 조회합니다.
	 * @author janghyun
	 * @return 모든 카테고리의 응답 DTO 목록
	 */
	List<CategoryResponse> getAllCategory();

	/**
	 * 특정 카테고리를 조회합니다.
	 * @author janghyun
	 * @param categoryId 조회할 카테고리의 ID
	 * @return 조회된 카테고리의 응답 DTO
	 */
	CategoryResponse getCategoryById(Long categoryId);

	/**
	 * 특정 카테고리를 업데이트합니다.
	 *
	 * @author janghyun
	 * @param dto 업데이트할 카테고리의 정보
	 * @param categoryId 업데이트할 카테고리의 ID
	 * @return 업데이트된 카테고리의 응답 DTO
	 */
	CategoryResponse updateCategory(CategoryRequest dto, Long categoryId);

	/**
	 * 특정 카테고리를 삭제합니다.
	 *
	 * @author janghyun
	 * @param categoryId 삭제할 카테고리의 ID
	 */
	void deleteCategory(Long categoryId);
}
