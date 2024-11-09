package shop.nuribooks.books.book.contributor.controller;

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
import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.service.ContributorService;
import java.util.List;

/**
 * @author kyongmin
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contributors")
public class ContributorController {

	private final ContributorService contributorService;

	/**
	 * 기여자 등록하는 controller
	 *
	 * @param request 등록할 기여자 이름을 담은 요청 객체
	 * @return 등록된 기여자 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "새 기여자 등록", description = "새로운 기여자를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "기여자 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping
	public ResponseEntity<ContributorResponse> registerContributor(@Valid @RequestBody ContributorRequest request) {
		ContributorResponse response = contributorService.registerContributor(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 특정 기여자 정보 수정하는 controller
	 *
	 * @param contributorId 수정할 기여자 id
	 * @param request 수정할 기여자 정보를 담은 요청 객체
	 * @return 수정된 기여자 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "기여자 정보 수정", description = "기존 기여자의 정보를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "기여자 정보 수정 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "기여자를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PutMapping("/{contributorId}")
	public ResponseEntity<ContributorResponse> updateContributor(
		@PathVariable Long contributorId, @Valid @RequestBody ContributorRequest request) {
		ContributorResponse response = contributorService.updateContributor(contributorId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 특정 기여자 조회하는 controller
	 *
	 * @param contributorId 조회할 기여자 id
	 * @return 기여자 정보와 상태코드 201 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "기여자 정보 조회", description = "ID를 사용해 특정 기여자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "기여자 조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "기여자를 찾을 수 없음")

	})
	@GetMapping("/{contributorId}")
	public ResponseEntity<ContributorResponse> getContributor(
		@PathVariable Long contributorId) {
		ContributorResponse response = contributorService.getContributor(contributorId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 모든 기여자 정보 조회하는 controller
	 *
	 * @return 모든 기여자 목록이 담긴 ResponseEntity
	 */
	@Operation(summary = "모든 기여자 정보 조회", description = "모든 기여자의 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "기여자 목록 조회 성공"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
			@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping
	public ResponseEntity<List<ContributorResponse>> getAllContributor() {
		List<ContributorResponse> contributorResponses = contributorService.getAllContributors();
		return ResponseEntity.status(HttpStatus.OK).body(contributorResponses);
	}

	/**
	 * 특정 기여자 삭제하는 controller
	 *
	 * @param contributorId 삭제할 기여자 id
	 * @return 상태코드 200 포함한 응답 ResponseEntity
	 */
	@Operation(summary = "기여자 삭제", description = "ID를 사용해 기존 기여자를 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "기여자 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "기여자를 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@DeleteMapping("{contributorId}")
	public ResponseEntity<HttpStatus> deleteContributor(@PathVariable Long contributorId) {
		contributorService.deleteContributor(contributorId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
