package shop.nuribooks.books.book.tag.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.service.TagService;
import shop.nuribooks.books.common.message.ResponseMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/tags")
public class TagController {
	private final TagService tagService;

	/**
	 * 태그 등록하는 controller
	 *
	 * @param request 등록할 태그 이름을 담은 요청 객체
	 * @return 등록된 태그 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "태그 등록", description = "새로운 태그를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "태그 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@PostMapping
	public ResponseEntity<ResponseMessage> registerTag(@Valid @RequestBody TagRequest request) {
		tagService.registerTag(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "태그 등록 성공"));
	}

	/**
	 * 모든 태그 조회하는 controller
	 *
	 * @return 등록된 모든 태그 정보와 상태코드 200 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "모든 태그 조회", description = "등록된 모든 태그를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "태그 조회 성공"),
	})
	@GetMapping
	public ResponseEntity<Page<TagResponse>> getAllTags(Pageable pageable) {
		Page<TagResponse> tagResponses = tagService.getAllTags(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(tagResponses);
	}

	@GetMapping("/all")
	public ResponseEntity<List<TagResponse>> getAllTags() {
		List<TagResponse> tagResponses = tagService.getAllTags();
		return ResponseEntity.status(HttpStatus.OK).body(tagResponses);
	}

	/**
	 * 특정 태그 조회하는 controller
	 *
	 * @param tagId 조회할 태그 id
	 * @return 해당하는 태그 정보와 상태코드 200 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "특정 태그 조회", description = "ID로 특정 태그를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "태그 조회 성공"),
		@ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
	})
	@GetMapping("/{tagId}")
	public ResponseEntity<TagResponse> getTag(@Valid @PathVariable Long tagId) {
		TagResponse response = tagService.getTag(tagId);
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}

	/**
	 * 특정 태그 수정하는 controller
	 *
	 * @param tagId 수정할 태그 id
	 * @param request 수정할 태그 이름을 담은 요청 객체
	 * @return 해당하는 태그 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "특정 태그 수정", description = "ID에 해당하는 태그를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "태그 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음")
	})
	@PutMapping("/{tagId}")
	public ResponseEntity<ResponseMessage> updateTag(@Valid @PathVariable Long tagId, @RequestBody TagRequest request) {
		tagService.updateTag(tagId, request);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "태그 수정 성공"));
	}

	/**
	 * 특정 태그 삭제하는 controller
	 *
	 * @param tagId 삭제할 태그 id
	 * @return 상태코드 200 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "태그 삭제", description = "ID에 해당하는 태그를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "태그 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "태그를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@DeleteMapping("/{tagId}")
	public ResponseEntity<HttpStatus> deleteTag(@Valid @PathVariable Long tagId) {
		tagService.deleteTag(tagId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
