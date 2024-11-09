package shop.nuribooks.books.member.cart.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.cart.dto.response.CartAddResponse;
import shop.nuribooks.books.member.cart.dto.response.CartListResponse;
import shop.nuribooks.books.member.cart.dto.response.CartUpdateResponse;
import shop.nuribooks.books.member.cart.service.CartService;

@WebMvcTest(CartController.class)
class CartControllerTest {

	@MockBean
	private CartService cartService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		MemberIdContext.setMemberId(1L);
	}

	@AfterEach
	void tearDown() {
		MemberIdContext.clear();
	}

	@DisplayName("장바구니 등록 성공")
	@Test
	void addToCart() throws Exception {
		//given
		Long memberId = MemberIdContext.getMemberId();
		Long bookId = 1L;
		int quantity = 3;
		CartAddResponse response = getCartAddResponse();

		when(cartService.addToCart(memberId, bookId, quantity)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(post(
			"/api/member/cart/{bookId}/{quantity}", bookId, quantity));

		//then
		result.andExpect(status().isCreated())
			.andExpect(jsonPath("bookId").value(response.bookId()))
			.andExpect(jsonPath("state").value(response.state().name()))
			.andExpect(jsonPath("title").value(response.title()))
			.andExpect(jsonPath("thumbnailImageUrl").value(response.thumbnailImageUrl()))
			.andExpect(jsonPath("price").value(response.price()))
			.andExpect(jsonPath("discountRate").value(response.discountRate()))
			.andExpect(jsonPath("isPackageable").value(response.isPackageable()))
			.andExpect(jsonPath("quantity").value(response.quantity()));
	}

	@DisplayName("장바구니 조회 성공")
	@Test
	void getCartList() throws Exception {
		//given
		Long memberId = MemberIdContext.getMemberId();

		List<CartListResponse> response = getCartListResponses();

		when(cartService.getCartList(memberId)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(get("/api/member/cart/me"));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].bookId").value(response.getFirst().bookId()))
			.andExpect(jsonPath("$[0].state").value(response.getFirst().state().name()))
			.andExpect(jsonPath("$[0].title").value(response.getFirst().title()))
			.andExpect(jsonPath("$[0].thumbnailImageUrl").value(response.getFirst().thumbnailImageUrl()))
			.andExpect(jsonPath("$[0].price").value(response.getFirst().price()))
			.andExpect(jsonPath("$[0].discountRate").value(response.getFirst().discountRate()))
			.andExpect(jsonPath("$[0].isPackageable").value(response.getFirst().isPackageable()))
			.andExpect(jsonPath("$[0].quantity").value(response.getFirst().quantity()))
			.andExpect(jsonPath("$.length()").value(1));
	}

	@DisplayName("장바구니 삭제 성공")
	@Test
	void cartDelete() throws Exception {
	    //given
		Long memberId = MemberIdContext.getMemberId();
		Long bookId= 1L;

		doNothing().when(cartService).deleteCart(memberId, bookId);

		//when
		ResultActions result = mockMvc.perform(delete("/api/member/cart/me/{bookId}", bookId));

	    //then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("statusCode").value("200"))
			.andExpect(jsonPath("message")
				.value("장바구니에서 상품이 삭제되었습니다."));
	}

	@DisplayName("장바구니 수정 성공")
	@Test
	void cartUpdate() throws Exception {
		//given
		Long memberId = MemberIdContext.getMemberId();
		Long bookId = 1L;
		int quantity = 5;
		CartUpdateResponse response = getCartUpdateResponses();

		when(cartService.updateCart(memberId, bookId, quantity)).thenReturn(response);

		//when
		ResultActions result = mockMvc.perform(patch(
			"/api/member/cart/me/{bookId}/{quantity}", bookId, quantity));

		//then
		result.andExpect(status().isOk())
			.andExpect(jsonPath("bookId").value(response.bookId()))
			.andExpect(jsonPath("state").value(response.state().name()))
			.andExpect(jsonPath("title").value(response.title()))
			.andExpect(jsonPath("thumbnailImageUrl").value(response.thumbnailImageUrl()))
			.andExpect(jsonPath("price").value(response.price()))
			.andExpect(jsonPath("discountRate").value(response.discountRate()))
			.andExpect(jsonPath("isPackageable").value(response.isPackageable()))
			.andExpect(jsonPath("quantity").value(response.quantity()));
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
	 * 테스트를 위한 CartAddResponse 생성
	 */
	private CartAddResponse getCartAddResponse() {
		return CartAddResponse.builder()
			.bookId(getSavedBook().getId())
			.state(getSavedBook().getState())
			.title(getSavedBook().getTitle())
			.thumbnailImageUrl(getSavedBook().getThumbnailImageUrl())
			.price(getSavedBook().getPrice())
			.discountRate(getSavedBook().getDiscountRate())
			.isPackageable(getSavedBook().isPackageable())
			.quantity(3)
			.build();
	}

	/**
	 * 테스트를 위한 CartListResponse 생성
	 */
	private List<CartListResponse> getCartListResponses() {
		return List.of(
			CartListResponse.builder()
			.bookId(getSavedBook().getId())
			.state(getSavedBook().getState())
			.title(getSavedBook().getTitle())
			.thumbnailImageUrl(getSavedBook().getThumbnailImageUrl())
			.price(getSavedBook().getPrice())
			.discountRate(getSavedBook().getDiscountRate())
			.isPackageable(getSavedBook().isPackageable())
			.quantity(3)
			.build());
	}

	/**
	 * 테스트를 위한 CartUpdateResponse 생성
	 */
	private CartUpdateResponse getCartUpdateResponses() {
		return CartUpdateResponse.builder()
			.bookId(getSavedBook().getId())
			.state(getSavedBook().getState())
			.title(getSavedBook().getTitle())
			.thumbnailImageUrl(getSavedBook().getThumbnailImageUrl())
			.price(getSavedBook().getPrice())
			.discountRate(getSavedBook().getDiscountRate())
			.isPackageable(getSavedBook().isPackageable())
			.quantity(3)
			.build();
	}
}