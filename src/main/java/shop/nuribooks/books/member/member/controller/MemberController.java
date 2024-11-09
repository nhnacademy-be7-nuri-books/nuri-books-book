package shop.nuribooks.books.member.member.controller;

import static org.springframework.http.HttpStatus.*;
import static shop.nuribooks.books.member.member.entity.AuthorityType.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.StatusType;
import shop.nuribooks.books.member.member.service.MemberService;

/**
 * @author Jprotection
 */
@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원등록 <br>
	 * MemberCreateReq의 모든 필드 즉, <br>
	 * name, username, password, phoneNumber, email, birthday에 대해서 검증 후 회원가입 진행 <br>
	 * 등록에 성공하면 name, username, phoneNumber, email, birthday를 <br>
	 * MemberRegisterResponse에 담아서 반환
	 */
	@Operation(summary = "신규 회원 등록", description = "신규 회원을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "신규 회원 등록 성공"),
		@ApiResponse(responseCode = "400", description = "신규 회원 등록 요청 데이터가 유효하지 않음"),
		@ApiResponse(responseCode = "409", description = "등록된 정보가 이미 존재함")
	})
	@PostMapping("/api/member")
	public ResponseEntity<MemberRegisterResponse> memberRegister(
		@RequestBody @Valid MemberRegisterRequest request) {

		MemberRegisterResponse response = memberService.registerMember(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * username으로 회원의 PK id와 비밀번호, 권한을 조회 <br>
	 * 회원이 존재하면 id, username, 비밀번호, 권한을 MemberAuthInfoResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 id, username, 비밀번호, 권한이 모두 null인 MemberAuthInfoResponse를 반환
	 */
	@Operation(summary = "username으로 회원 인증 조회", description = "username으로 회원의 PK id와 비밀번호, 권한을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 인증 조회 성공")
	})
	@GetMapping("/api/member/username/{username}")
	public ResponseEntity<MemberAuthInfoResponse> getMemberAuthInfoByUsername(@PathVariable String username) {

		MemberAuthInfoResponse response = memberService.getMemberAuthInfoByUsername(username);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 이메일로 회원의 PK id와 username, 비밀번호, 권한을 조회 <br>
	 * 회원이 존재하면 id, username, 비밀번호, 권한을 MemberAuthInfoResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 id, username, 비밀번호, 권한이 모두 null인 MemberAuthInfoResponse를 반환
	 */
	@Operation(summary = "이메일로 회원 인증 조회", description = "이메일로 회원의 PK id와 username, 비밀번호, 권한을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 인증 조회 성공")
	})
	@GetMapping("/api/member/email/{email}")
	public ResponseEntity<MemberAuthInfoResponse> getMemberAuthInfoByEmail(@PathVariable String email) {

		MemberAuthInfoResponse response = memberService.getMemberAuthInfoByEmail(email);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * ThreadLocal에서 회원의 PK id를 가져와서 탈퇴 진행
	 */
	@Operation(summary = "회원 탈퇴", description = "회원의 PK id로 회원을 탈퇴합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
		@ApiResponse(responseCode = "404", description = "회원이 존재하지 않음")
	})
	@DeleteMapping("/api/member/me")
	public ResponseEntity<ResponseMessage> memberWithdraw() {

		Long memberId = MemberIdContext.getMemberId();
		memberService.withdrawMember(memberId);

		return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(),
			"탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	/**
	 * ThreadLocal에서 회원의 PK id를 가져와서 회원 상세 정보 조회 <br>
	 * MemberDetailsResponse에 회원의 모든 정보를 담아서 반환
	 */
	@Operation(summary = "회원 상세 조회", description = "회원의 PK id로 회원의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 상세 조회 성공"),
		@ApiResponse(responseCode = "404", description = "회원이 존재하지 않음")
	})
	@GetMapping("/api/member/me")
	public ResponseEntity<MemberDetailsResponse> getMemberDetails() {

		Long memberId = MemberIdContext.getMemberId();
		MemberDetailsResponse response = memberService.getMemberDetails(memberId);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 회원 정보 수정 <br>
	 * MemberUpdateReq의 모든 필드 즉, <br>
	 * name, password에 대해서 검증 후 userId를 통해 수정 진행 <br>
	 * MemberUpdateResponse에 변경한 이름과 전화번호 담아서 반환
	 */
	@Operation(summary = "회원 정보 수정", description = "유저 아이디로 회원 정보를 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
		@ApiResponse(responseCode = "400", description = "회원 정보 수정 요청 데이터가 유효하지 않음"),
		@ApiResponse(responseCode = "404", description = "회원이 존재하지 않음")
	})
	@PostMapping("/api/member/me")
	public ResponseEntity<ResponseMessage> memberUpdate(
		@RequestBody @Valid MemberUpdateRequest request) {

		Long memberId = MemberIdContext.getMemberId();
		memberService.updateMember(memberId, request);

		return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(),
			"회원 정보가 수정되었습니다."));
	}

	/**
	 * 관리자가 다양한 검색 조건을 이용하여 회원 목록 조회
	 */
	@Operation(summary = "관리자가 다양한 검색 조건으로 회원 조회", description = "관리자가 다양한 검색 조건을 이용하여 회원 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "관리자가 회원 목록 조회 성공")
	})
	@GetMapping("/admin/api/member/members")
	public ResponseEntity<Page<MemberSearchResponse>> memberSearchWithPaging(
		@RequestParam(value = "name", required = false) String name,
		@RequestParam(value = "email", required = false) String email,
		@RequestParam(value = "phoneNumber", required = false) String phoneNumber,
		@RequestParam(value = "gender", required = false) String gender,
		@RequestParam(value = "status", required = false) String status,
		Pageable pageable) {

		MemberSearchRequest request = MemberSearchRequest.builder()
			.name(name)
			.email(email)
			.phoneNumber(phoneNumber)
			.gender(GenderType.fromValue(gender))
			.status(StatusType.fromValue(status))
			.build();

		Page<MemberSearchResponse> response = memberService.searchMembersWithPaging(request, pageable);

		return ResponseEntity.status(OK).body(response);
	}
}
