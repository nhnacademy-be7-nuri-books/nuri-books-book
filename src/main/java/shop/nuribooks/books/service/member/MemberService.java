package shop.nuribooks.books.service.member;

import shop.nuribooks.books.dto.member.request.MemberRegisterRequest;
import shop.nuribooks.books.dto.member.request.MemberUpdateRequest;
import shop.nuribooks.books.dto.member.request.MemberWithdrawRequest;
import shop.nuribooks.books.dto.member.response.MemberCheckResponse;
import shop.nuribooks.books.dto.member.response.MemberRegisterResponse;
import shop.nuribooks.books.dto.member.response.MemberUpdateResponse;

public interface MemberService {

	/**
	 * 회원등록
	 */
	MemberRegisterResponse registerMember(MemberRegisterRequest request);

	/**
	 * 회원탈퇴
	 */
	void withdrawMember(MemberWithdrawRequest request);

	/**
	 * 회원 정보 수정
	 */
	MemberUpdateResponse updateMember(String userId, MemberUpdateRequest request);

	/**
	 * 회원 등록 여부 확인
	 */
	MemberCheckResponse checkMember(String userId);
}
