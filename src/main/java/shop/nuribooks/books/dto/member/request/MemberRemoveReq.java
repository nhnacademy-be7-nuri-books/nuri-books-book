package shop.nuribooks.books.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@NotBlank
@AllArgsConstructor
public class MemberRemoveReq {

	private String userId;
	private String password;
}
