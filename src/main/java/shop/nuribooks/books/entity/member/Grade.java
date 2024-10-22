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
@NotNull
public class Grade {

	@Id
	@Column(name = "grade_id")
	private Integer id;

	@NotBlank
	private String name; // STANDARD, ROYAL, GOLD, PLATINUM

	private Integer pointRate;

	@Column(precision = 10, scale = 2)
	private BigDecimal requirement;
}
