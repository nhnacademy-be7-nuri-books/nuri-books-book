package shop.nuribooks.books.member.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCheckResponse {

	private String name;
	private String password;
	private String authority; // "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_SELLER"
}
