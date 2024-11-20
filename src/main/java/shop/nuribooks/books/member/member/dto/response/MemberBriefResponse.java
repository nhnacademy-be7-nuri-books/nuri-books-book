package shop.nuribooks.books.member.member.dto.response;

import lombok.Builder;
import shop.nuribooks.books.member.member.entity.Member;

/**
 * member의 간단한 정보를 담은 dto
 * @param id
 * @param username
 */
@Builder
public record MemberBriefResponse(
	long id,
	String username
) {
	public static MemberBriefResponse of(Member member) {
		return MemberBriefResponse.builder()
			.id(member.getId())
			.username(member.getUsername())
			.build();
	}
}
