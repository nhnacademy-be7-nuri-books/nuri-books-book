package shop.nuribooks.books.book.elasticsearch.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;
import shop.nuribooks.books.book.elasticsearch.service.BookSearchService;
import shop.nuribooks.books.common.provider.IndexNameProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl implements BookSearchService {

	private final ElasticsearchClient elasticsearchClient;
	private final IndexNameProvider indexNameProvider; // Elasticsearch 인덱스 이름

	/**
	 * 책 검색 (검색 쿼리 DSL)
	 */
	public Page<BookDocument> searchBooks(String keyword, SearchType searchType, Pageable pageable) {
		SearchRequest request = SearchRequest.of(s -> s
			.index(indexNameProvider.resolveIndexName())
			.query(q -> q
				.bool(b -> searchType.apply(b, keyword)
				)
			)
			.from((int)pageable.getOffset())
			.size(pageable.getPageSize())
		);

		SearchResponse<BookDocument> response = null;

		try {
			response = elasticsearchClient.search(request, BookDocument.class);
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}

		List<BookDocument> books = response.hits().hits().stream()
			.map(Hit::source)
			.collect(Collectors.toList());

		return new PageImpl<>(books, pageable, response.hits().total().value());
	}
}
