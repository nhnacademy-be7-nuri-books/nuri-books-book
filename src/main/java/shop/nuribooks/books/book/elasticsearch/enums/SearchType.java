package shop.nuribooks.books.book.elasticsearch.enums;

import java.util.function.BiFunction;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.Builder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.ObjectBuilder;

public enum SearchType {
	ALL((b, keyword) -> b.should(sq -> titleBuilder(sq, keyword, 100.0f))
		.should(sq -> descriptionBuilder(sq, keyword, 10.0f))
		.should(sq -> tagsBuilder(sq, keyword, 50.0f))
		.should(sq -> contributorBuilder(sq, keyword, 30.0f))),
	TITLE((b, keyword) -> b.should(sq -> titleBuilder(sq, keyword, 1.0f))),
	DESCRIPTION((b, keyword) -> b.should(sq -> descriptionBuilder(sq, keyword, 1.0f))),
	CONTRIBUTOR((b, keyword) -> b.should(sq -> contributorBuilder(sq, keyword, 1.0f)));

	private BiFunction<Builder, String, Builder> func;

	SearchType(BiFunction<Builder, String, Builder> func) {
		this.func = func;
	}

	public static SearchType convert(String str) {
		SearchType type;
		try {
			type = SearchType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			type = SearchType.ALL;
		}
		return type;
	}

	private static ObjectBuilder<Query> titleBuilder(Query.Builder sq, String keyword, Float boost) {
		return sq.match(m -> m
			.field("title")
			.query(keyword)
			.boost(boost)
		);
	}

	private static ObjectBuilder<Query> descriptionBuilder(Query.Builder sq, String keyword, Float boost) {
		return sq.match(m -> m
			.field("description")
			.query(keyword)
			.boost(boost)
		);
	}

	private static ObjectBuilder<Query> tagsBuilder(Query.Builder sq, String keyword, Float boost) {
		return sq.match(m -> m
			.field("tags")
			.query(keyword)
			.boost(boost)
		);
	}

	private static ObjectBuilder<Query> contributorBuilder(Query.Builder sq, String keyword, Float boost) {
		return sq.nested(n -> n
			.path("contributors") // Nested 필드의 경로
			.query(nq -> nq
				.match(m -> m
					.field("contributors.name") // Nested 필드 내의 name
					.query(keyword)
					.boost(boost)
				)
			)
		);
	}

	public Builder apply(Builder b, String keyword) {
		b.minimumShouldMatch("1");
		return this.func.apply(b, keyword);
	}
}
