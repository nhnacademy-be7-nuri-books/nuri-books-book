package shop.nuribooks.books.book.point.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.enums.HistoryType;
import shop.nuribooks.books.book.point.service.PointHistoryService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/point-history")
public class PointHistoryController {
	private final PointHistoryService pointHistoryService;

	@Operation(summary = "포인트 내역 목록 조회", description = "포인터 내역 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
	})
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping
	public ResponseEntity<Page<PointHistoryResponse>> getPointHistories(
		@RequestParam(value = "type") HistoryType type,
		Pageable pageable,
		PointHistoryPeriodRequest pointHistoryPeriodRequest) {

		long memberId = MemberIdContext.getMemberId();

		Page<PointHistoryResponse> response = pointHistoryService.getPointHistories(memberId, type, pageable,
			pointHistoryPeriodRequest);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
