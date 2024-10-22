package shop.nuribooks.books.entity.member;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class Grade {

	@Id
	@Column(name = "grade_id")
	private Integer id;

	/**
	 * STANDARD, GOLD, PLATINUM, ROYAL
	 */
	@NotBlank
	private String name;

	@NotNull
	private Integer pointRate;

	@NotNull
	@Column(precision = 10, scale = 2)
	private BigDecimal requirement;
}
