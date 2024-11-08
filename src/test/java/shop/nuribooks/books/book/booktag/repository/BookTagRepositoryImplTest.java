package shop.nuribooks.books.book.booktag.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.booktag.entity.QBookTag;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.tag.entity.QTag;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.common.TestUtils;

class BookTagRepositoryImplTest {

	@InjectMocks
	private BookTagRepositoryImpl bookTagRepository;

	@Mock
	private JPAQueryFactory queryFactory;

	private QBookTag qBookTag = QBookTag.bookTag;
	private QTag qTag = QTag.tag;

	private Book book;
	private Tag tag;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		book = Book.builder()
			.publisherId(new Publisher(1L, "Sample Publisher"))
			.state(BookStateEnum.NEW)
			.title("Sample Book")
			.thumbnailImageUrl("https://example.com/thumbnail.jpg")
			.detailImageUrl("https://example.com/detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(29.99))
			.discountRate(10)
			.description("Sample description.")
			.contents("Sample contents.")
			.isbn("978-3-16-148410-0")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();
		TestUtils.setIdForEntity(book, 1L);

		tag = Tag.builder().id(2L).name("study").build();
		TestUtils.setIdForEntity(tag, 2L);
	}

	@DisplayName("주어진 도서 ID로 태그 이름을 조회한다")
	@Test
	void findTagNamesByBookId_Success() {
		// Given
		Long bookId = 1L;
		List<String> expectedTagNames = Arrays.asList("Fiction", "Adventure");

		JPAQuery<String> query = mock(JPAQuery.class);

		when(queryFactory.select(qTag.name)).thenReturn(query);
		when(query.from(qBookTag)).thenReturn(query);
		when(query.join(qBookTag.tag, qTag)).thenReturn(query);
		when(query.where(qBookTag.book.id.eq(bookId))).thenReturn(query);
		when(query.fetch()).thenReturn(expectedTagNames); // Final fetch

		// When
		List<String> actualTagNames = bookTagRepository.findTagNamesByBookId(bookId);

		// Then
		assertEquals(expectedTagNames, actualTagNames);
		verify(queryFactory, times(1)).select(qTag.name);
		verify(query, times(1)).from(qBookTag);
		verify(query, times(1)).join(qBookTag.tag, qTag);
		verify(query, times(1)).where(qBookTag.book.id.eq(bookId));
		verify(query, times(1)).fetch();
	}

	@DisplayName("주어진 태그 ID로 도서 ID를 조회한다")
	@Test
	void findBookIdsByTagId_Success() {
		// Given
		Long tagId = 2L;
		List<Long> expectedBookIds = Arrays.asList(1L, 2L);

		JPAQuery<Long> query = mock(JPAQuery.class);

		when(queryFactory.select(qBookTag.book.id)).thenReturn(query);
		when(query.from(qBookTag)).thenReturn(query);
		when(query.where(qBookTag.tag.id.eq(tagId))).thenReturn(query);
		when(query.fetch()).thenReturn(expectedBookIds); // Final fetch

		// When
		List<Long> actualBookIds = bookTagRepository.findBookIdsByTagId(tagId);

		// Then
		assertEquals(expectedBookIds, actualBookIds);
		verify(queryFactory, times(1)).select(qBookTag.book.id);
		verify(query, times(1)).from(qBookTag);
		verify(query, times(1)).where(qBookTag.tag.id.eq(tagId));
		verify(query, times(1)).fetch();
	}
}
