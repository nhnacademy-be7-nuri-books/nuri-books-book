package shop.nuribooks.books.book.elasticsearch.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ShardStatistics;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;
import shop.nuribooks.books.book.elasticsearch.enums.SortType;
import shop.nuribooks.books.book.elasticsearch.service.impl.BookSearchServiceImpl;
import shop.nuribooks.books.common.provider.IndexNameProvider;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {
	@InjectMocks
	private BookSearchServiceImpl bookSearchService;

	@Mock
	private ElasticsearchClient elasticsearchClient;

	@Mock
	private IndexNameProvider indexNameProvider;

	@Test
	void searchBooksTest() throws IOException {
		String keyword = "Java";
		Long categoryId = 1l;
		SearchType searchType = SearchType.ALL;
		SortType sortType = SortType.ACCURACY;
		PageRequest pageable = PageRequest.of(0, 10);

		// Mocked response
		BookDocument book1 = new BookDocument();
		book1.setId(1L);
		book1.setTitle("Java in Action");
		book1.setDescription("Comprehensive guide to Java");
		book1.setPrice(BigDecimal.valueOf(35.99));

		BookDocument book2 = new BookDocument();
		book2.setId(2L);
		book2.setTitle("Advanced Java");
		book2.setDescription("Deep dive into Java features");
		book2.setPrice(BigDecimal.valueOf(45.99));

		List<Hit<BookDocument>> hits = List.of(
			Hit.of(h -> h
				.index("nuribooks") // Required 'index' field
				.id("1")            // Required '_id' field
				.source(book1)      // Document source
			),
			Hit.of(h -> h
				.index("nuribooks")
				.id("2")
				.source(book2)
			)
		);

		TotalHits totalHits = TotalHits.of(th -> th
			.value(2) // Total number of hits
			.relation(TotalHitsRelation.Eq) // Exact match count
		);

		// Mocked HitsMetadata
		HitsMetadata<BookDocument> hitsMetadata = HitsMetadata.of(h -> h
			.total(totalHits) // Set total hits
			.hits(hits) // Set the hits list
		);

		// Mocked ShardStatistics
		ShardStatistics shardStats = ShardStatistics.of(s -> s
			.total(1)
			.successful(1)
			.skipped(0)
			.failed(0)
		);

		// Mocked SearchResponse
		SearchResponse<BookDocument> mockResponse = SearchResponse.of(r -> r
			.took(10)
			.timedOut(false)
			.shards(shardStats)
			.hits(hitsMetadata) // Use the mocked HitsMetadata
		);

		when(indexNameProvider.resolveIndexName()).thenReturn("test");
		when(elasticsearchClient.search(any(SearchRequest.class), eq(BookDocument.class))).thenReturn(mockResponse);

		// When
		Page<BookDocument> result = bookSearchService.searchBooks(keyword, categoryId, searchType, sortType, pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		assertEquals("Java in Action", result.getContent().get(0).getTitle());
		assertEquals("Advanced Java", result.getContent().get(1).getTitle());
	}

	@Test
	void searchBooksTestWithCategory() throws IOException {
		String keyword = "Java";
		Long categoryId = null;
		SearchType searchType = SearchType.ALL;
		SortType sortType = SortType.ACCURACY;
		PageRequest pageable = PageRequest.of(0, 10);

		// Mocked response
		BookDocument book1 = new BookDocument();
		book1.setId(1L);
		book1.setTitle("Java in Action");
		book1.setDescription("Comprehensive guide to Java");
		book1.setPrice(BigDecimal.valueOf(35.99));

		BookDocument book2 = new BookDocument();
		book2.setId(2L);
		book2.setTitle("Advanced Java");
		book2.setDescription("Deep dive into Java features");
		book2.setPrice(BigDecimal.valueOf(45.99));

		List<Hit<BookDocument>> hits = List.of(
			Hit.of(h -> h
				.index("nuribooks") // Required 'index' field
				.id("1")            // Required '_id' field
				.source(book1)      // Document source
			),
			Hit.of(h -> h
				.index("nuribooks")
				.id("2")
				.source(book2)
			)
		);

		TotalHits totalHits = TotalHits.of(th -> th
			.value(2) // Total number of hits
			.relation(TotalHitsRelation.Eq) // Exact match count
		);

		// Mocked HitsMetadata
		HitsMetadata<BookDocument> hitsMetadata = HitsMetadata.of(h -> h
			.total(totalHits) // Set total hits
			.hits(hits) // Set the hits list
		);

		// Mocked ShardStatistics
		ShardStatistics shardStats = ShardStatistics.of(s -> s
			.total(1)
			.successful(1)
			.skipped(0)
			.failed(0)
		);

		// Mocked SearchResponse
		SearchResponse<BookDocument> mockResponse = SearchResponse.of(r -> r
			.took(10)
			.timedOut(false)
			.shards(shardStats)
			.hits(hitsMetadata) // Use the mocked HitsMetadata
		);

		when(indexNameProvider.resolveIndexName()).thenReturn("test");
		when(elasticsearchClient.search(any(SearchRequest.class), eq(BookDocument.class))).thenReturn(mockResponse);

		// When
		Page<BookDocument> result = bookSearchService.searchBooks(keyword, categoryId, searchType, sortType, pageable);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		assertEquals("Java in Action", result.getContent().get(0).getTitle());
		assertEquals("Advanced Java", result.getContent().get(1).getTitle());
	}
}
