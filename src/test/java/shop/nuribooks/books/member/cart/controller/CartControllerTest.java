package shop.nuribooks.books.member.cart.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.member.cart.dto.CartAddResponse;
import shop.nuribooks.books.member.cart.entity.Cart;
import shop.nuribooks.books.member.cart.service.CartService;
import shop.nuribooks.books.member.member.entity.AuthorityType;
import shop.nuribooks.books.member.member.entity.GenderType;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.entity.StatusType;

@WebMvcTest(CartController.class)
class CartControllerTest {

	@MockBean
	private CartService cartService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@DisplayName("장바구니 등록 성공")
	@Test
	void addToCart() throws Exception {
		//given
		Long memberId = 1L;
		Long bookId = 1L;
		CartAddResponse response = getCartAddResponse();

		when(cartService.addToCart(memberId, bookId)).thenReturn(response);

		//when
		ResultActions result = mockMvc
			.perform(post("/api/member/cart/{memberId}/{bookId}", memberId, bookId)
			.contentType(APPLICATION_JSON));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("state").value(response.state().name()))
			.andExpect(jsonPath("title").value(response.title()))
			.andExpect(jsonPath("thumbnailImageUrl").value(response.thumbnailImageUrl()))
			.andExpect(jsonPath("price").value(response.price()))
			.andExpect(jsonPath("discountRate").value(response.discountRate()))
			.andExpect(jsonPath("isPackageable").value(response.isPackageable()));
	}


	/**
	 * 테스트를 위한 회원 생성
	 */
	private Member getSavedMember() {
		return Member.builder()
			.customer(null)
			.authority(AuthorityType.MEMBER)
			.grade(null)
			.status(StatusType.ACTIVE)
			.gender(GenderType.MALE)
			.userId("nuribooks95")
			.birthday(LocalDate.of(1988, 8, 12))
			.createdAt(LocalDateTime.now())
			.point(BigDecimal.ZERO)
			.totalPaymentAmount(BigDecimal.ZERO)
			.build();
	}

	/**
	 * 테스트를 위한 도서 생성
	 */
	private Book getSavedBook() {
		return Book.builder()
			.publisherId(null)
			.state(BookStateEnum.NORMAL)
			.title("Original Book Title")
			.thumbnailImageUrl("original_thumbnail.jpg")
			.detailImageUrl("original_detail.jpg")
			.publicationDate(LocalDate.now())
			.price(BigDecimal.valueOf(20000))
			.discountRate(10)
			.description("Original Description")
			.contents("Original Contents")
			.isbn("1234567890123")
			.isPackageable(true)
			.stock(100)
			.likeCount(0)
			.viewCount(0L)
			.build();
	}

	/**
	 * 테스트를 위한 장바구니 생성
	 */
	private Cart getSavedCart() {
		return Cart.builder()
			.member(getSavedMember())
			.book(getSavedBook())
			.build();
	}

	/**
	 * 테스트를 위한 CartAddResponse 생성
	 */
	private CartAddResponse getCartAddResponse() {
		return CartAddResponse.builder()
			.state(getSavedBook().getState())
			.title(getSavedBook().getTitle())
			.thumbnailImageUrl(getSavedBook().getThumbnailImageUrl())
			.price(getSavedBook().getPrice())
			.discountRate(getSavedBook().getDiscountRate())
			.isPackageable(getSavedBook().isPackageable())
			.build();
	}
}