package shop.nuribooks.books.book.tag.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.service.TagService;

@RestController
@RequiredArgsConstructor
public class TagController {
	private final TagService tagService;

	/**
	 * 태그 등록하는 controller
	 * @author kyongmiin
	 *
	 * @param request 등록할 태그 이름을 담은 요청 객체
	 * @return 등록된 태그 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "태그 등록", description = "새로운 태그를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "태그 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@PostMapping("/api/books/tags")
	public ResponseEntity<TagResponse> registerTag(@Valid @RequestBody TagRequest request) {
		TagResponse response = tagService.registerTag(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
