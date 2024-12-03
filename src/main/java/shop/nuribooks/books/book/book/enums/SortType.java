package shop.nuribooks.books.book.book.enums;

import static shop.nuribooks.books.book.book.entity.QBook.*;
import static shop.nuribooks.books.book.review.entity.QReview.*;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;

public enum SortType {
	TITLE_ASC(book.title, Order.ASC),
	TITLE_DESC(book.title, Order.DESC),
	NEW(book.publicationDate, Order.ASC),
	CHEAP(book.price, Order.ASC),
	EXPENSIVE(book.price, Order.DESC),
	STAR_DESC(review.score.avg(), Order.DESC),
	STAR_ASC(review.score.avg(), Order.ASC),
	REVIEW_COUNT_DESC(review.id.count(), Order.DESC),
	REVIEW_COUNT_ASC(review.id.count(), Order.ASC);

	private Expression<?> exp;
	private Order order;

	<T> SortType(Expression<T> exp, Order order) {
		this.exp = exp;
		this.order = order;
	}

	public static SortType convert(String str) {
		SortType type;
		try {
			type = SortType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			type = SortType.TITLE_ASC;
		}
		return type;
	}

	public Expression getExpression() {
		return this.exp;
	}

	public Order getOrder() {
		return this.order;
	}
}
