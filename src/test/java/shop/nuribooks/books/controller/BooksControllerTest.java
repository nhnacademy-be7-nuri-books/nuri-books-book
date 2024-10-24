package shop.nuribooks.books.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.controller.book.BookController;
import shop.nuribooks.books.dto.book.BookRegisterReq;
import shop.nuribooks.books.dto.book.BookRegisterRes;
import shop.nuribooks.books.exception.BadRequestException;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.service.book.BookService;

@WebMvcTest(BookController.class)
public class BooksControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookService booksService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void registerBooks_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
		BookRegisterReq reqDto = new BookRegisterReq(
			1,
			1L,
			"책 제목",
			"thumbnail.jpg",
			"detail.jpg",
			LocalDate.parse("2024-10-21"),
			BigDecimal.valueOf(10000),
			0,
			"책 설명",
			"책 내용",
			"1234567890123",
			true,
			10
		);

		BookRegisterRes resDto = BookRegisterRes.builder()
			.id(1L)
			.stateId(1)
			.publisherId(1L)
			.title("책 제목")
			.thumbnailImageUrl("thumbnail.jpg")
			.publicationDate(LocalDate.parse("2024-10-21"))
			.price(BigDecimal.valueOf(10000))
			.discountRate(0)
			.description("책 설명")
			.stock(10)
			.build();

		when(booksService.registerBook(any(BookRegisterReq.class))).thenReturn(resDto);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.title").value("책 제목"));
	}

	@Test
	public void registerBooks_ShouldReturnBadRequest_WhenRequestIsInvalid() throws Exception {
		BookRegisterReq reqDto = new BookRegisterReq(
			null,
			(Long)null,
			"",
			null,
			null,
			null,
			null,
			-1,
			null,
			null,
			null,
			false,
			-1
		);

		when(booksService.registerBook(any(BookRegisterReq.class)))
			.thenThrow(new BadRequestException("잘못된 요청 데이터입니다."));

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void registerBooks_ShouldReturnNotFound_WhenBookStateOrPublisherNotFound() throws Exception {
		BookRegisterReq reqDto = new BookRegisterReq(
			9999,
			9999L,
			"책 제목",
			"thumbnail.jpg",
			"detail.jpg",
			LocalDate.now(),
			BigDecimal.valueOf(10000),
			0,
			"책 설명",
			"책 내용",
			"1234567890123",
			true,
			10
		);

		when(booksService.registerBook(any(BookRegisterReq.class)))
			.thenThrow(new ResourceNotFoundException("해당 id를 찾지 못했습니다."));

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(reqDto)))
			.andExpect(status().isNotFound());
	}
}
