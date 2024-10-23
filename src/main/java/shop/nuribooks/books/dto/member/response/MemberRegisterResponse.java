package shop.nuribooks.books.dto.member.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRegisterResponse {

	private String name;
	private String userId;
	private String phoneNumber;
	private String email;
	private LocalDate birthday;

}
