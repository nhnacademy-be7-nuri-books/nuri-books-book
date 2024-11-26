package shop.nuribooks.books.book.coupon.controller;

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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.coupon.dto.MemberCouponIssueRequest;
import shop.nuribooks.books.book.coupon.dto.MemberCouponResponse;
import shop.nuribooks.books.book.coupon.service.MemberCouponService;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-coupons")
@Tag(name = "MemberCoupon", description = "회원 쿠폰 관리 API")
public class MemberCouponController {

	private final MemberCouponService memberCouponService;

	/**
	 * 회원에게 쿠폰을 등록합니다.
	 *
	 * @param memberCouponIssueRequest 쿠폰 등록을 위한 정보를 포함한 요청
	 * @return 성공 시 상태 코드 201 반환
	 */
	@Operation(summary = "회원 쿠폰 등록", description = "회원에게 쿠폰을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "회원 쿠폰 등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "404", description = "회원 또는 쿠폰을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PostMapping
	public ResponseEntity<Void> registerMemberCoupon(
		@RequestBody MemberCouponIssueRequest memberCouponIssueRequest) {
		memberCouponService.registerMemberCoupon(memberCouponIssueRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@HasRole(role = AuthorityType.MEMBER)
	@PostMapping("/{coupon-id}")
	public ResponseEntity<Void> issueMemberToBookCoupon(@Valid @PathVariable(name = "coupon-id") Long couponId) {
		Long memberId = MemberIdContext.getMemberId();

		MemberCouponRegisterRequest memberCouponRegisterRequest = new MemberCouponRegisterRequest(memberId, couponId);
		memberCouponService.registerMemberCoupon(memberCouponRegisterRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원 ID로 사용 가능한 쿠폰을 조회합니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 사용 가능한 쿠폰 정보를 담은 페이지
	 */
	@Operation(summary = "사용 가능한 쿠폰 조회", description = "회원 ID로 사용 가능한 쿠폰을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용 가능한 쿠폰 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping("/available")
	public ResponseEntity<Page<MemberCouponResponse>> getAvailableCouponsByMemberId(Pageable pageable) {

		Long memberId = MemberIdContext.getMemberId();

		Page<MemberCouponResponse> coupons = memberCouponService.getAvailableCouponsByMemberId(memberId, pageable);
		return ResponseEntity.ok(coupons);
	}

	/**
	 * 회원 ID로 만료되었거나 사용된 쿠폰을 조회합니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 만료되었거나 사용된 쿠폰 정보를 담은 페이지
	 */
	@Operation(summary = "만료 또는 사용된 쿠폰 조회", description = "회원 ID로 만료되었거나 사용된 쿠폰을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "만료 또는 사용된 쿠폰 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@HasRole(role = AuthorityType.MEMBER)
	@GetMapping("/expired-or-used")
	public ResponseEntity<Page<MemberCouponResponse>> getExpiredOrUsedCouponsByMemberId(Pageable pageable) {

		Long memberId = MemberIdContext.getMemberId();

		Page<MemberCouponResponse> coupons = memberCouponService.getExpiredOrUsedCouponsByMemberId(memberId, pageable);
		return ResponseEntity.ok(coupons);
	}

	/**
	 * 회원 ID로 모든 쿠폰을 조회합니다.
	 *
	 * @param memberId 회원 ID
	 * @return 회원의 모든 쿠폰 정보를 담은 MemberCouponResponse 리스트
	 */
	@Operation(summary = "회원 쿠폰 조회", description = "회원 ID로 모든 쿠폰을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 쿠폰 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/{memberId}")
	public ResponseEntity<List<MemberCouponResponse>> getAllCouponsByMemberId(@PathVariable Long memberId) {
		List<MemberCouponResponse> coupons = memberCouponService.getAllCouponByMemberId(memberId);
		return ResponseEntity.ok(coupons);
	}

	/**
	 * 쿠폰 사용 상태를 업데이트합니다.
	 *
	 * @param memberCouponId 업데이트할 회원 쿠폰 ID
	 * @return 성공 시 상태 코드 200 반환
	 */
	@Operation(summary = "회원 쿠폰 사용 업데이트", description = "회원 쿠폰의 사용 상태를 업데이트합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 쿠폰 사용 업데이트 성공"),
		@ApiResponse(responseCode = "404", description = "회원 쿠폰을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@PutMapping("/{memberCouponId}")
	public ResponseEntity<Void> updateIsUsed(@PathVariable Long memberCouponId) {
		memberCouponService.updateIsUsed(memberCouponId);
		return ResponseEntity.ok().build();
	}

	/**
	 * 회원 쿠폰을 삭제합니다.
	 *
	 * @param memberCouponId 삭제할 회원 쿠폰 ID
	 * @return 성공 시 상태 코드 204 반환
	 */
	@Operation(summary = "회원 쿠폰 삭제", description = "회원 쿠폰을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "회원 쿠폰 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "회원 쿠폰을 찾을 수 없음"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@DeleteMapping("/{memberCouponId}")
	public ResponseEntity<Void> deleteMemberCoupon(@PathVariable Long memberCouponId) {
		memberCouponService.deleteMemberCoupon(memberCouponId);
		return ResponseEntity.noContent().build();
	}
}
