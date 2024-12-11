package shop.nuribooks.books.book.booklike.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.book.booklike.service.BookLikeService;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

@WebMvcTest(BookLikeController.class)
class BookLikeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookLikeService bookLikeService;

	@Test
	void addLike() throws Exception {
		Long memberId = 1L;
		MemberIdContext.setMemberId(memberId);
		Long bookId = 1L;

		doNothing().when(bookLikeService).addLike(memberId, bookId);

		mockMvc.perform(post("/api/books/book-likes/{book-id}", bookId))
			.andExpect(status().isCreated());

		verify(bookLikeService, times(1)).addLike(memberId, bookId);
	}

	@Test
	void removeLike() throws Exception {
		Long memberId = 1L;
		MemberIdContext.setMemberId(memberId);
		Long bookId = 1L;

		doNothing().when(bookLikeService).removeLike(memberId, bookId);

		mockMvc.perform(delete("/api/books/book-likes/{book-id}", bookId))
			.andExpect(status().isNoContent());

		verify(bookLikeService, times(1)).removeLike(memberId, bookId);
	}

	@Test
	void getBookLikes() throws Exception {
		Long memberId = 1L;
		MemberIdContext.setMemberId(memberId);

		Publisher publisher = Publisher.builder()
			.name("누리북스")
			.build();

		Book book2 = Book.builder()
			.publisherId(publisher)
			.price(BigDecimal.valueOf(20000))
			.state(BookStateEnum.NORMAL)
			.thumbnailImageUrl("thumbnail2.jpg")
			.title("Book Title 2")
			.build();

		ReflectionTestUtils.setField(book2, "id", 2L);

		BookLikeResponse response = BookLikeResponse.builder()
			.bookId(book2.getId())
			.title(book2.getTitle())
			.publisherName(book2.getPublisherId().getName())
			.price(book2.getPrice())
			.discountRate(book2.getDiscountRate())
			.salePrice(BookUtils.calculateSalePrice(book2.getPrice(), book2.getDiscountRate()))
			.thumbnailImageUrl(book2.getThumbnailImageUrl())
			.contributorsByRole(null)
			.build();

		when(bookLikeService.getLikedBooks(eq(memberId), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(response)));

		mockMvc.perform(get("/api/books/book-likes/me"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void getLikeStatus() throws Exception {
		Long bookId = 1L;
		Long memberId = 1L;
		MemberIdContext.setMemberId(memberId);

		when(bookLikeService.isBookLikedByMember(memberId, bookId)).thenReturn(false);

		mockMvc.perform(get("/api/books/book-likes/status/{book-id}", bookId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.isLiked").value(false));

		verify(bookLikeService, times(1)).isBookLikedByMember(memberId, bookId);
	}
}
