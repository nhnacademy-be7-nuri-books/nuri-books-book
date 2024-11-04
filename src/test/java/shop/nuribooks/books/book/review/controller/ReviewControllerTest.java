package shop.nuribooks.books.book.review.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.review.dto.request.ReviewRegisterRequest;
import shop.nuribooks.books.book.review.dto.response.ReviewImageResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;
import shop.nuribooks.books.book.review.service.ReviewService;

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
		ReviewRegisterRequest reviewRequest = new ReviewRegisterRequest(
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

		when(reviewService.registerReview(any(ReviewRegisterRequest.class), eq(1L))).thenReturn(response);

		// Act & Assert
		mockMvc.perform(post("/api/reviews")
				.header("X-USER-ID", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reviewRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.title").value("title"));
	}
}

