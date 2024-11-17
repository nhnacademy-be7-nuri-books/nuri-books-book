package shop.nuribooks.books.book.elasticsearch.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.service.BookSearchService;
import shop.nuribooks.books.book.publisher.entity.Publisher;
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
	public Page<BookDocument> searchBooks(String keyword, Pageable pageable) {
		SearchRequest request = SearchRequest.of(s -> s
			.index(indexNameProvider.resolveIndexName())
			.query(q -> q
				.bool(b -> b
					.should(sq -> sq
						.match(m -> m
							.field("title")
							.query(keyword)
							.boost(100.0f)
						)
					)
					.should(sq -> sq
						.match(m -> m
							.field("description")
							.query(keyword)
							.boost(10.0f)
						)
					)
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

	/**
	 * 책 저장
	 */
	@Override
	public BookDocument saveBook(Book book, Publisher publisher) {
		BookDocument bookDocument = BookDocument.of(book, publisher);
		IndexRequest<BookDocument> request = IndexRequest.of(i -> i
			.index(indexNameProvider.resolveIndexName())
			.id(bookDocument.getId().toString())
			.document(bookDocument)
		);

		IndexResponse response = null;
		try {
			response = elasticsearchClient.index(request);
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("Book saved with ID: {}, Result: {}", response.id(), response.result().jsonValue());

		return bookDocument;
	}

	/**
	 * 책 업데이트
	 */
	@Override
	public void updateBook(String id, BookDocument bookDocument) {
		UpdateRequest<BookDocument, JsonData> request = UpdateRequest.of(u -> u
			.index(indexNameProvider.resolveIndexName())
			.id(id)
			.doc(JsonData.of(bookDocument)) // 업데이트할 필드를 JSON 형태로 전달
		);

		UpdateResponse<BookDocument> response = null;
		try {
			response = elasticsearchClient.update(request, BookDocument.class);
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("Book updated with ID: {}, Result: {}", id, response.result().jsonValue());
	}

	/**
	 * 책 삭제
	 */
	public void deleteBook(String id) {
		DeleteRequest request = DeleteRequest.of(d -> d
			.index(indexNameProvider.resolveIndexName())
			.id(id)
		);

		DeleteResponse response = null;
		try {
			response = elasticsearchClient.delete(request);
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("Book deleted with ID: {}, Result: {}", id, response.result().jsonValue());
	}

	/**
	 * 책 대량 저장
	 */
	public void bulkSaveBooks(List<BookDocument> books) {
		BulkRequest.Builder bulkRequest = new BulkRequest.Builder();

		for (BookDocument book : books) {
			bulkRequest.operations(op -> op
				.index(idx -> idx
					.index(indexNameProvider.resolveIndexName())
					.id(book.getId().toString())
					.document(book)
				)
			);
		}

		BulkResponse response = null;
		try {
			response = elasticsearchClient.bulk(bulkRequest.build());
		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}
		if (response.errors()) {
			log.error("Bulk save had errors: {}", response.toString());
		} else {
			log.info("Bulk save completed successfully.");
		}
	}
}
