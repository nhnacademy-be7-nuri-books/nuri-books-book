package shop.nuribooks.books.member.member.service;

import shop.nuribooks.books.member.member.dto.request.MemberDetailsRequest;
import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberUpdateRequest;
import shop.nuribooks.books.member.member.dto.request.MemberWithdrawRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberUpdateResponse;

/**
 * @author Jprotection
 */
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
	 * 회원 인증 정보 조회
	 */
	MemberAuthInfoResponse getMemberAuthInfo(String userId);

	/**
	 * 회원 상세 조회
	 */
	MemberDetailsResponse getMemberDetails(MemberDetailsRequest request);
}
