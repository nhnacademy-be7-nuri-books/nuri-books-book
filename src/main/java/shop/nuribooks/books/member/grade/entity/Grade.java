package shop.nuribooks.books.member.grade.entity;

import static jakarta.persistence.GenerationType.*;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "grades")
public class Grade {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "grade_id")
	private Integer id;

	/**
	 * STANDARD, ROYAL, GOLD, PLATINUM
	 */
	@NotBlank
	@Column(unique = true)
	private String name;

	@NotNull
	private Integer pointRate;

	@NotNull
	@Column(precision = 11, scale = 2)
	private BigDecimal requirement;

	public void changeGradeInformation(String name, Integer pointRate, BigDecimal requirement) {
		this.name = name;
		this.pointRate = pointRate;
		this.requirement = requirement;
	}
}
