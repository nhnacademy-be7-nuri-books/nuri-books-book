package shop.nuribooks.books.controller.member;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.dto.member.ResponseMessage;
import shop.nuribooks.books.dto.member.request.MemberCreateReq;
import shop.nuribooks.books.dto.member.request.MemberRemoveReq;
import shop.nuribooks.books.service.member.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원가입
	 */
	@PostMapping("/api/member")
	public ResponseEntity<ResponseMessage> memberCreate(
		@RequestBody @Valid MemberCreateReq request, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			FieldError first = bindingResult.getFieldErrors().getFirst();

		}

		memberService.memberCreate(request);

		return ResponseEntity.status(CREATED).build();
	}

	/**
	 * 아이디로 회원 등록 여부 확인
	 */
	@GetMapping("/api/member/{userId}")
	public ResponseEntity<ResponseMessage> isMemberExist(@PathVariable String userId) {
		if (memberService.isMemberExist(userId)) {
			return ResponseEntity.status(OK).body(new ResponseMessage(OK.value(), "존재하는 회원입니다."));
		}
		return ResponseEntity.status(NOT_FOUND)
			.body(new ResponseMessage(NOT_FOUND.value(), "존재하지 않는 회원입니다."));
	}

	/**
	 * 아이디와 비밀번호로 회원 탈퇴
	 */
	// @PostMapping("/api/member")
	// public ResponseEntity<ResponseMessage> memberRemove(@RequestBody MemberRemoveReq request) {
	// 	memberService.memberRemove(request);
	// }
}
