package shop.nuribooks.books.controller.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.dto.category.CategoryResponse;
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
	 */
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryServiceImpl) {
		this.categoryService = categoryServiceImpl;
	}

	/**
	 * 새로운 대분류를 등록합니다.
	 * @author janghyun
	 * @param dto 카테고리 등록 세부 정보를 포함한 요청 본문
	 * @return 생성된 카테고리 정보를 담은 ResponseEntity
	 */
	@Operation(summary = "새로운 대분류 등록", description = "새로운 대분류 카테고리를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping
	public ResponseEntity<CategoryResponse> registerMainCategory(@Valid @RequestBody CategoryRequest dto) {
		CategoryResponse categoryResponse = new CategoryResponse(
			categoryService.registerMainCategory(dto));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(categoryResponse);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류를 등록합니다.
	 * @author janghyun
	 * @param dto 하위 분류 등록 세부 정보를 포함한 요청 본문
	 * @param categoryId 하위 분류가 등록될 대분류의 ID
	 * @return 생성된 하위 분류 정보를 담은 ResponseEntity
	 */
	@Operation(summary = "하위 분류 등록", description = "기존 대분류 아래에 새로운 하위 분류를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "하위 분류 생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "대분류를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> registerSubCategory(@Valid @RequestBody CategoryRequest dto,
		@PathVariable Long categoryId) {
		CategoryResponse categoryResponse = new CategoryResponse(
			categoryService.registerSubCategory(dto, categoryId));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(categoryResponse);
	}

	/**
	 * 모든 카테고리를 조회합니다.
	 *
	 * @author janghyun
	 * @return 모든 카테고리의 응답 리스트
	 */
	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categoryResponseList = categoryService.getAllCategory();
		return ResponseEntity.ok(categoryResponseList);
	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회합니다.
	 *
	 * @author janghyun
	 * @param categoryId 조회할 카테고리의 ID
	 * @return 조회된 카테고리의 응답 객체와 그 하위 카테고리
	 */
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
		CategoryResponse categoryResponse = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(categoryResponse);
	}

}