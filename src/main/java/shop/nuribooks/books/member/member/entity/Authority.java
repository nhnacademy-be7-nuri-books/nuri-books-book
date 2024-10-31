package shop.nuribooks.books.member.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Getter
public class Authority {

	@Id
	private Integer id;

	/**
	 * ADMIN, MEMBER, SELLER
	 */
	@NotBlank
	private String name;
}
