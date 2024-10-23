package shop.nuribooks.books.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.entity.member.AuthorityEnum;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCheckResponse {

	private String name;
	private String password;
	private AuthorityEnum authority;
}
