package shop.nuribooks.books.book.review.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.review.dto.request.ReviewRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.service.ReviewService;
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReviewService reviewService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registerTest() throws Exception {
		ReviewRequest reviewRequest = new ReviewRequest(
			"title",
			"contentcontent",
			4,
			1L,
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
		);

		ReviewMemberResponse response = new ReviewMemberResponse(
			0,
			"title",
			"contentcontent",
			4,
			null,
			List.of(new ReviewImageResponse(1, "http://example.com/image1.jpg"),
				new ReviewImageResponse(2, "http://example.com/image2.jpg"))
		);

		when(reviewService.registerReview(any(ReviewRequest.class))).thenReturn(response);

		// header 추가 대신 set
		MemberIdContext.setMemberId(1l);
		// Act & Assert
		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reviewRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("title"));
	}

	@Test
	void getReviewMemberTest() throws Exception {
		long bookId = 1;
		ReviewMemberResponse review = new ReviewMemberResponse(
			0,
			"title",
			"contentcontent",
			4,
			null,
			List.of(new ReviewImageResponse(1, "http://example.com/image1.jpg"),
				new ReviewImageResponse(2, "http://example.com/image2.jpg"))
		);

		when(reviewService.getReviewsByBookId(anyLong(), any())).thenReturn(
			new PageImpl<>(List.of(review)));

		mockMvc.perform(get("/api/books/" + bookId + "/reviews"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(11)));
	}

	@Test
	void getReviewBookTest() throws Exception {
		long memberId = 1;
		ReviewBookResponse review = new ReviewBookResponse(
			0,
			"title",
			"contentcontent",
			4,
			null,
			List.of(new ReviewImageResponse(1, "http://example.com/image1.jpg"),
				new ReviewImageResponse(2, "http://example.com/image2.jpg"))
		);

		when(reviewService.getReviewsByMemberId(anyLong(), any())).thenReturn(
			new PagedResponse<>(List.of(review), 1, 1, 1, 1));

		mockMvc.perform(get("/api/members/" + memberId + "/reviews"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", Matchers.hasSize(5)));
	}

	@Test
	void updateTest() throws Exception {
		ReviewRequest reviewRequest = new ReviewRequest(
			"title",
			"contentnew",
			4,
			1L,
			List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
		);

		ReviewMemberResponse response = new ReviewMemberResponse(
			0,
			"title",
			"contentnew",
			4,
			null,
			List.of(new ReviewImageResponse(1, "http://example.com/image1.jpg"),
				new ReviewImageResponse(2, "http://example.com/image2.jpg"))
		);

		when(reviewService.updateReview(any(ReviewRequest.class), anyLong())).thenReturn(response);

		// header 추가 대신 set
		MemberIdContext.setMemberId(1l);
		// Act & Assert
		mockMvc.perform(put("/api/reviews/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reviewRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("title"))
			.andExpect(jsonPath("$.content").value("contentnew"));
	}
}

