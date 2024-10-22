package shop.nuribooks.books.entity.member;

import java.math.BigDecimal;

public enum GradeEnum {

	STANDARD(3, BigDecimal.valueOf(100_000)),
	GOLD(5, BigDecimal.valueOf(200_000)),
	PLATINUM(8, BigDecimal.valueOf(350_000)),
	ROYAL(12, BigDecimal.valueOf(500_000));

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
