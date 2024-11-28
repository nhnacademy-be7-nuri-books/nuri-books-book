package shop.nuribooks.books.book.elasticsearch.enums;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;

public enum SortType {
	ACCURACY(SortOptions.of(so -> so.score(ss -> ss.order(SortOrder.Desc)))),
	// 구현 예정
	POPULAR(SortOptions.of(so -> so.field(f -> f.field("view_count").order(SortOrder.Desc)))),

	NEW(SortOptions.of(so -> so.field(f -> f.field("publication_date").order(SortOrder.Desc)))),
	CHEAP(SortOptions.of(so -> so.field(f -> f.field("price").order(SortOrder.Asc)))),
	EXPENSIVE(SortOptions.of(so -> so.field(f -> f.field("price").order(SortOrder.Desc)))),
	STAR(SortOptions.of(so -> so.field(f -> f.field("total_score").order(SortOrder.Desc)))),
	REVIEW_COUNT(SortOptions.of(so -> so.field(f -> f.field("review_count").order(SortOrder.Desc))));

	private SortOptions sortOptions;

	SortType(SortOptions sortOptions) {
		this.sortOptions = sortOptions;
	}

	public static SortType convert(String str) {
		SortType type;
		try {
			type = SortType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			type = SortType.ACCURACY;
		}
		return type;
	}

	public SortOptions getSortOptions() {
		return this.sortOptions;
	}
}
