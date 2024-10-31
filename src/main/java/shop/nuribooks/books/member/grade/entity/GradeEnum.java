package shop.nuribooks.books.member.grade.entity;

import java.math.BigDecimal;

public enum GradeEnum {

	STANDARD(1, BigDecimal.valueOf(100_000)),
	ROYAL(2, BigDecimal.valueOf(200_000)),
	GOLD(2, BigDecimal.valueOf(300_000)),
	PLATINUM(3, BigDecimal.valueOf(500_000));

	/**
	 * 할인율
	 */
	private final Integer pointRate;

	/**
	 * 요구되는 금액 조건
	 */
	private final BigDecimal requirement;

	GradeEnum(Integer pointRate, BigDecimal requirement) {
		this.pointRate = pointRate;
		this.requirement = requirement;
	}
}
