package shop.nuribooks.books.book.point.dto.request.register;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import shop.nuribooks.books.book.point.entity.PointHistory;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.member.member.entity.Member;

@AllArgsConstructor
public class PointHistoryRequest {
	@NotNull
	Member member;
	@NotNull
	PointPolicy pointPolicy;

	public PointHistory toEntity() {
		return PointHistory.builder()
			.amount(pointPolicy.getAmount())
			.description(pointPolicy.getName())
			.member(member)
			.pointPolicy(pointPolicy)
			.build();
	}
}
