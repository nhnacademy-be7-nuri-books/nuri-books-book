package shop.nuribooks.books.book.point.controller;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.service.PointPolicyService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point-policies")
public class PointPolicyController {
	private final PointPolicyService pointPolicyService;

	@Operation(summary = "포인트 정책 목록 조회", description = "포인터 정책 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@GetMapping
	public ResponseEntity<List<PointPolicyResponse>> getPointPolicies() {
		List<PointPolicyResponse> pointPolicyResponses = this.pointPolicyService.getPointPolicyResponses();
		return ResponseEntity.status(HttpStatus.OK).body(pointPolicyResponses);
	}

	@Operation(summary = "포인트 정책 생성", description = "포인터 정책을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "생성 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PostMapping
	public ResponseEntity<ResponseMessage> registerPointPolicy(
		@Valid @RequestBody PointPolicyRequest pointPolicyRequest) {
		this.pointPolicyService.registerPointPolicy(pointPolicyRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseMessage(HttpStatus.CREATED.value(), "포인트 정책 생성 성공"));
	}

	@Operation(summary = "포인트 정책 업데이트", description = "포인터 정책을 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "업데이트 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@PutMapping("/{point_policy_id}")
	public ResponseEntity<ResponseMessage> updatePointPolicy(@PathVariable("point_policy_id") long id,
		@Valid @RequestBody PointPolicyRequest pointPolicyRequest) {
		this.pointPolicyService.updatePointPolicy(id, pointPolicyRequest);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "포인트 정책 업데이트 성공"));
	}

	@Operation(summary = "포인트 정책 삭제", description = "포인터 정책을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.ADMIN)
	@DeleteMapping("/{point_policy_id}")
	public ResponseEntity<ResponseMessage> deletePointPolicy(@PathVariable("point_policy_id") long id) {
		this.pointPolicyService.deletePointPolicy(id);
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseMessage(HttpStatus.OK.value(), "포인트 정책 삭제 성공"));
	}
}
