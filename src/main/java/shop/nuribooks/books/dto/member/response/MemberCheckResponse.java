package shop.nuribooks.books.dto.member.response;

import com.fasterxml.jackson.annotation.JsonValue;

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
	private String authority; // "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_SELLER"
}
