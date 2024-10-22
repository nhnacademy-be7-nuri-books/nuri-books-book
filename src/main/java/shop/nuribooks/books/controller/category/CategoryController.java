package shop.nuribooks.books.controller.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import shop.nuribooks.books.dto.category.request.CategoryRegisterReq;
import shop.nuribooks.books.dto.category.response.CategoryRegisterRes;
import shop.nuribooks.books.service.category.CategoryService;

/**
 * 카테고리와 관련된 작업을 처리하는 컨트롤러.
 * 대분류와 하위 분류를 생성하는 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	/**
	 * CategoryController의 생성자.
	 *
	 * @param categoryServiceImpl 카테고리 서비스 구현체
	 */
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryServiceImpl) {
		this.categoryService = categoryServiceImpl;
	}

	/**
	 * 새로운 대분류를 등록합니다.
	 *
	 * @param dto 카테고리 등록 세부 정보를 포함한 요청 본문
	 * @return 생성된 카테고리 정보를 담은 ResponseEntity
	 */
	@PostMapping
	public ResponseEntity<CategoryRegisterRes> registerMainCategory(@Valid @RequestBody CategoryRegisterReq dto) {
		CategoryRegisterRes categoryRegisterRes = new CategoryRegisterRes(
			categoryService.registerMainCategory(dto));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(categoryRegisterRes);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류를 등록합니다.
	 *
	 * @param dto 하위 분류 등록 세부 정보를 포함한 요청 본문
	 * @param categoryId 하위 분류가 등록될 대분류의 ID
	 * @return 생성된 하위 분류 정보를 담은 ResponseEntity
	 */
	@PostMapping("/{categoryId}")
	public ResponseEntity<CategoryRegisterRes> registerSubCategory(@Valid @RequestBody CategoryRegisterReq dto,
		@PathVariable Long categoryId) {
		CategoryRegisterRes categoryRegisterRes = new CategoryRegisterRes(
			categoryService.registerSubCategory(dto, categoryId));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(categoryRegisterRes);
	}
}
