package shop.nuribooks.books.book.point.enums;

import static shop.nuribooks.books.book.point.entity.QPointHistory.*;

import java.math.BigDecimal;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.Getter;

public enum HistoryType {
	// 각 조건별로 실행해야 할 함수 실행.
	ALL(null),
	USED(pointHistory.amount.lt(BigDecimal.ZERO)),
	SAVED(pointHistory.amount.goe(BigDecimal.ZERO));

	@Getter
	private BooleanExpression be;

	HistoryType(BooleanExpression be) {
		this.be = be;
	}

	public static HistoryType convert(String str) {
		HistoryType type;
		try {
			type = HistoryType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			type = HistoryType.ALL;
		}
		return type;
	}
}
