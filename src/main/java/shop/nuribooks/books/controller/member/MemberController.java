package shop.nuribooks.books.controller.member;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.dto.member.request.MemberCreateReq;
import shop.nuribooks.books.dto.member.request.MemberUpdateReq;
import shop.nuribooks.books.dto.member.request.MemberWithdrawReq;
import shop.nuribooks.books.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원가입 <br>
	 * MemberCreateReq의 모든 필드 즉, <br>
	 * name, userId, password, phoneNumber, email, birthday에 대해서 검증 후 회원가입 진행
	 */
	@PostMapping("/api/member")
	public ResponseEntity<ResponseMessage> memberCreate(
		@RequestBody @Valid MemberCreateReq request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getFieldErrors().getFirst().getDefaultMessage();

			return ResponseEntity.status(BAD_REQUEST).body(new ResponseMessage(BAD_REQUEST.value(), errorMessage));
		}

		memberService.createMember(request);

		return ResponseEntity.status(CREATED)
			.body(new ResponseMessage(CREATED.value(), "회원가입이 성공적으로 완료되었습니다!"));
	}

	/**
	 * 아이디로 회원 등록 여부 확인 <br>
	 * pathVariable로 입력된 userId 길이 검사 후 회원 확인 진행
	 */
	@GetMapping("/api/member/{userId}")
	public ResponseEntity<ResponseMessage> memberDoesExist(@PathVariable String userId) {
		if (userId.length() < 8 || userId.length() > 20) {
			return ResponseEntity.status(BAD_REQUEST)
				.body(new ResponseMessage(BAD_REQUEST.value(), "아이디는 반드시 8자 이상 20자 이하로 입력해야 합니다."));
		}

		if (memberService.doesMemberExist(userId)) {
			return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(), "존재하는 회원입니다."));
		}

		return ResponseEntity.status(NOT_FOUND)
			.body(new ResponseMessage(NOT_FOUND.value(), "존재하지 않는 회원입니다."));
	}

	/**
	 * 아이디와 비밀번호로 회원 탈퇴 <br>
	 * MemberWithdrawReq의 모든 필드 즉, <br>
	 * userId와 password에 대해서 검증 후 회원 탈퇴 진행
	 */
	@PatchMapping("/api/member/status")
	public ResponseEntity<ResponseMessage> memberWithdraw(
		@RequestBody @Valid MemberWithdrawReq request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getFieldErrors().getFirst().getDefaultMessage();

			return ResponseEntity.status(BAD_REQUEST).body(new ResponseMessage(BAD_REQUEST.value(), errorMessage));
		}

		memberService.withdrawMember(request);

		return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(),
			"탈퇴가 성공적으로 완료되었습니다. 귀하의 앞날에 무궁한 발전이 있기를 진심으로 기원하겠습니다."));
	}

	/**
	 * 로그인 상태의 사용자 정보 수정 <br>
	 * MemberUpdateReq의 모든 필드 즉, <br>
	 * name, password, phoneNumber에 대해서 검증 후 userId를 통해 수정 진행
	 */
	@PatchMapping("/api/member/{userId}")
	public ResponseEntity<ResponseMessage> memberUpdate(
		@PathVariable String userId, @RequestBody @Valid MemberUpdateReq request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getFieldErrors().getFirst().getDefaultMessage();

			return ResponseEntity.status(BAD_REQUEST).body(new ResponseMessage(BAD_REQUEST.value(), errorMessage));
		}

		memberService.updateMember(userId, request);

		return ResponseEntity.status(OK)
			.body(new ResponseMessage(OK.value(), "회원 수정이 성공적으로 완료되었습니다."));
	}
}
