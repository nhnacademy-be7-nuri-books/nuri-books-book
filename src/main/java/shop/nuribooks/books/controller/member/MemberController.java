package shop.nuribooks.books.controller.member;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.dto.member.request.MemberRegisterRequest;
import shop.nuribooks.books.dto.member.request.MemberUpdateRequest;
import shop.nuribooks.books.dto.member.request.MemberWithdrawRequest;
import shop.nuribooks.books.dto.member.response.MemberCheckResponse;
import shop.nuribooks.books.dto.member.response.MemberRegisterResponse;
import shop.nuribooks.books.dto.member.response.MemberUpdateResponse;
import shop.nuribooks.books.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원등록 <br>
	 * MemberCreateReq의 모든 필드 즉, <br>
	 * name, userId, password, phoneNumber, email, birthday에 대해서 검증 후 회원가입 진행
	 * 등록에 성공하면 name, userId, phoneNumber, email, birthday를 <br>
	 * MemberRegisterResponse에 담아서 반환
	 */
	@PostMapping("/api/member")
	public ResponseEntity<MemberRegisterResponse> memberRegister(
		@RequestBody @Valid MemberRegisterRequest request) {

		MemberRegisterResponse response = memberService.registerMember(request);

		return ResponseEntity.status(CREATED).body(response);
	}

	/**
	 * 아이디로 회원 조회 <br>
	 * pathVariable로 입력된 userId 길이 검사 후 회원 확인 진행 <br>
	 * 회원이 존재하면 이름, 비밀번호, 권한을 MemberCheckResponse에 담아서 반환 <br>
	 * 회원이 존재하지 않는다면 이름, 비밀번호, 권한이 모두 null인 MemberCheckResponse를 반환
	 */
	@GetMapping("/api/member/{userId}")
	public ResponseEntity<MemberCheckResponse> memberCheck(@PathVariable String userId) {

		MemberCheckResponse response = memberService.checkMember(userId);

		return ResponseEntity.status(OK).body(response);
	}

	/**
	 * 아이디와 비밀번호로 회원 탈퇴 <br>
	 * MemberWithdrawRequest의 모든 필드 즉, <br>
	 * userId와 password에 대해서 검증 후 회원 탈퇴 진행
	 */
	@PatchMapping("/api/member/status")
	public ResponseEntity<ResponseMessage> memberWithdraw(
		@RequestBody MemberWithdrawRequest request) {

		memberService.withdrawMember(request);

		return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(),
			"탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	/**
	 * 회원 정보 수정 <br>
	 * MemberUpdateReq의 모든 필드 즉, <br>
	 * name, password, phoneNumber에 대해서 검증 후 userId를 통해 수정 진행 <br>
	 * MemberUpdateResponse에 변경한 이름과 전화번호 담아서 반환
	 */
	@PostMapping("/api/member/{userId}")
	public ResponseEntity<MemberUpdateResponse> memberUpdate(
		@PathVariable String userId, @RequestBody @Valid MemberUpdateRequest request) {

		MemberUpdateResponse response = memberService.updateMember(userId, request);

		return ResponseEntity.status(OK).body(response);
	}

}
