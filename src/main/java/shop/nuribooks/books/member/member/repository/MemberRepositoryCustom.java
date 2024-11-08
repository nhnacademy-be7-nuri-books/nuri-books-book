package shop.nuribooks.books.member.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.member.member.dto.request.MemberSearchRequest;
import shop.nuribooks.books.member.member.dto.response.MemberSearchResponse;

/**
 * @author Jprotection
 */
public interface MemberRepositoryCustom {

	Page<MemberSearchResponse> searchMembersWithPaging(MemberSearchRequest request, Pageable pageable);
}
