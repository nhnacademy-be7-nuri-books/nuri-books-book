package shop.nuribooks.books.book.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import shop.nuribooks.books.book.category.dto.CategoryRequest;
import shop.nuribooks.books.book.category.dto.CategoryResponse;
import shop.nuribooks.books.book.category.service.CategoryService;
import shop.nuribooks.books.common.message.ResponseMessage;

/**
 * 카테고리와 관련된 작업을 처리하는 컨트롤러.
 * 대분류와 하위 분류를 생성하는 엔드포인트를 제공합니다.
 *
 * @author janghyun
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
	 *
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
	public ResponseEntity<ResponseMessage> registerMainCategory(@Valid @RequestBody CategoryRequest dto) {
		categoryService.registerMainCategory(dto);

		ResponseMessage responseMessage = new ResponseMessage(201, "카테고리가 성공적으로 생성되었습니다.");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
	}

	/**
	 * 기존 대분류 아래에 새로운 하위 분류를 등록합니다.
	 *
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
	public ResponseEntity<ResponseMessage> registerSubCategory(@Valid @RequestBody CategoryRequest dto,
		@PathVariable Long categoryId) {
		categoryService.registerSubCategory(dto, categoryId);

		ResponseMessage responseMessage = new ResponseMessage(201, "카테고리가 성공적으로 생성되었습니다.");

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(responseMessage);
	}

	/**
	 * 모든 카테고리를 조회합니다.
	 *
	 * @return 모든 카테고리의 응답 리스트
	 */
// TODO : 캐싱 테스트 후 활성화
//	@Operation(summary = "모든 카테고리 조회", description = "모든 카테고리를 조회합니다.")
//	@ApiResponses(value = {
//		@ApiResponse(responseCode = "200", description = "성공적으로 모든 카테고리를 조회하였습니다.")
//	})
//
//	@GetMapping
//	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
//		List<CategoryResponse> categoryResponseList = categoryService.getAllCategory();
//		return ResponseEntity.ok(categoryResponseList);
//	}

	/**
	 * 주어진 ID에 해당하는 카테고리를 조회합니다.
	 *
	 * @param categoryId 조회할 카테고리의 ID
	 * @return 조회된 카테고리의 응답 객체와 그 하위 카테고리
	 */
	@Operation(summary = "카테고리 조회", description = "주어진 ID에 해당하는 카테고리를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 카테고리를 조회하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 ID의 카테고리를 찾을 수 없습니다.")
	})
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
		CategoryResponse categoryResponse = categoryService.getCategoryById(categoryId);
		return ResponseEntity.ok(categoryResponse);
	}

	/**
	 * 특정 카테고리를 업데이트합니다.
	 *
	 * @param dto 업데이트할 카테고리의 정보
	 * @param categoryId 업데이트할 카테고리의 ID
	 * @return 업데이트된 카테고리의 응답 DTO
	 */
	@Operation(summary = "카테고리 업데이트", description = "주어진 ID에 해당하는 카테고리를 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 카테고리를 업데이트하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 ID의 카테고리를 찾을 수 없습니다."),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
	})
	@PutMapping("/{categoryId}")
	public ResponseEntity<ResponseMessage> updateCategory(
		@Valid
		@Parameter(description = "업데이트할 카테고리의 정보", required = true)
		@RequestBody CategoryRequest dto,
		@Parameter(description = "업데이트할 카테고리의 ID", required = true)
		@PathVariable Long categoryId) {

		categoryService.updateCategory(dto, categoryId);

		ResponseMessage responseMessage = new ResponseMessage(200, "카테고리가 성공적으로 수정되었습니다.");

		return ResponseEntity.ok().body(responseMessage);
	}

	/**
	 * 특정 카테고리를 삭제합니다.
	 *
	 * @param categoryId 삭제할 카테고리의 ID
	 * @return 삭제 결과를 나타내는 응답 메시지
	 */
	@Operation(summary = "카테고리 삭제", description = "주어진 ID에 해당하는 카테고리를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "성공적으로 카테고리를 삭제하였습니다."),
		@ApiResponse(responseCode = "404", description = "해당 ID의 카테고리를 찾을 수 없습니다."),
	})
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(
		@Parameter(description = "업데이트할 카테고리의 ID", required = true)
		@PathVariable Long categoryId
	) {
		categoryService.deleteCategory(categoryId);

		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "카테고리 트리 조회", description = "트리 구조로 모든 카테고리를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 카테고리 트리를 조회하였습니다.")
	})
	@GetMapping("/tree")
	public ResponseEntity<List<CategoryResponse>> getAllCategoryTree() {
		List<CategoryResponse> categoryTrees = categoryService.getAllCategoryTree();
		return ResponseEntity.status(HttpStatus.OK).body(categoryTrees);
	}

	@GetMapping("/{category-id}/name")
	public ResponseEntity<CategoryRequest> getCategoryName(@PathVariable(name = "category-id") Long categoryId) {
		CategoryRequest categoryName = categoryService.getCategoryNameById(categoryId);
		return ResponseEntity.status(HttpStatus.OK).body(categoryName);
	}
}
