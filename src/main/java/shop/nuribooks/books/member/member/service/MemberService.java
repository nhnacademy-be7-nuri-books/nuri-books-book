package shop.nuribooks.books.member.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.member.member.dto.request.MemberRegisterRequest;
import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.request.MemberPasswordUpdateRequest;
import shop.nuribooks.books.member.member.dto.response.MemberAuthInfoResponse;
import shop.nuribooks.books.member.member.dto.response.MemberDetailsResponse;
import shop.nuribooks.books.member.member.dto.response.MemberRegisterResponse;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;

/**
 * @author Jprotection
 */
public interface MemberService {

	MemberRegisterResponse registerMember(MemberRegisterRequest request);

	void withdrawMember(Long memberId);

	void updateMember(Long memberId, MemberPasswordUpdateRequest request);

	MemberAuthInfoResponse getMemberAuthInfoByUsername(String username);

	MemberAuthInfoResponse getMemberAuthInfoByEmail(String email);

	MemberDetailsResponse getMemberDetails(Long memberId);

	Page<MemberSearchResponse> searchMembersWithPaging(MemberSearchRequest request, Pageable pageable);

	void updateMemberLatestLoginAt(Long memberId);
}
