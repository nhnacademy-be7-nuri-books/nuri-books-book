package shop.nuribooks.books.service.member;

import shop.nuribooks.books.dto.member.request.MemberRegisterRequest;
import shop.nuribooks.books.dto.member.request.MemberUpdateRequest;
import shop.nuribooks.books.dto.member.request.MemberWithdrawRequest;

public interface MemberService {

	/**
	 * 회원등록
	 */
	void registerMember(MemberRegisterRequest request);

	/**
	 * 회원탈퇴
	 */
	void withdrawMember(MemberWithdrawRequest request);

	/**
	 * 회원 정보 수정
	 */
	void updateMember(String userId, MemberUpdateRequest request);

	/**
	 * 회원 등록 여부 확인
	 */
	boolean doesMemberExist(String userId);
}
