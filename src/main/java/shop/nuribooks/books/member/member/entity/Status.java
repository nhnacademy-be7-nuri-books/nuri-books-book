package shop.nuribooks.books.member.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Entity
@Getter
public class Status {

	@Id
	private Integer id;

	/**
	 * ACTIVE, INACTIVE, WITHDRAWN
	 */
	@NotBlank
	private String name;
}
