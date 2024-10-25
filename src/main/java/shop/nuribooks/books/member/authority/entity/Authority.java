package shop.nuribooks.books.member.authority.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.member.authority.dto.requset.AuthorityEditRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Authority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * ADMIN, MEMBER, SELLER
	 */
	@Enumerated(EnumType.STRING)
	private AuthorityEnum name;

	@Builder
	private Authority(AuthorityEnum name) {
		this.name = name;
	}

	public void editAuthority(AuthorityEditRequest request){
		this.name = request.authorityEnum();
	}
}
