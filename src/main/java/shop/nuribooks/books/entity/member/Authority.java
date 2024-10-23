package shop.nuribooks.books.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Getter
public class Authority {

	@Id
	@Column(name = "authority_id")
	private Integer id;

	/**
	 * ADMIN, MEMBER, SELLER
	 */
	@NotBlank
	private String name;
}
