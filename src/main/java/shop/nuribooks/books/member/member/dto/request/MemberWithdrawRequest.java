package shop.nuribooks.books.member.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberWithdrawRequest {

	private String userId;
	private String password;
}
