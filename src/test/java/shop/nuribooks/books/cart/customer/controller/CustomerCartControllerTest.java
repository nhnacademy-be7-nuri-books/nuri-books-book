package shop.nuribooks.books.cart.customer.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.cart.customer.dto.request.CustomerCartAddRequest;
import shop.nuribooks.books.cart.customer.dto.response.CustomerCartResponse;
import shop.nuribooks.books.cart.customer.service.CustomerCartService;

@WebMvcTest(controllers = CustomerCartController.class)
class CustomerCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CustomerCartService customerCartService;

    @DisplayName("비회원 장바구니에 도서를 담는다.")
    @Test
    void addCustomerCart() throws Exception {
        // given
        CustomerCartAddRequest request = new CustomerCartAddRequest( 1L, 1);

        // when then
        mockMvc.perform(post("/api/customer/cart")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("비회원 장바구니 리스트를 가져온다")
    @Test
    void getCustomerCartList() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        Long bookId1 = 1L;
        List<String> tagNames = new ArrayList<>();
        tagNames.add("wow");

        BigDecimal price = BigDecimal.valueOf(10000);
        int discountRate = 10;
        BigDecimal salePrice = price
            .multiply(BigDecimal.valueOf(100 - discountRate))
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);

        Map<String, List<String>> contributorsByRole = new HashMap<>();
        contributorsByRole.put("지은이", List.of("유재령", "이영애", "차효정"));
        contributorsByRole.put("감수", List.of("김광웅"));

        BookResponse bookResponse1 = new BookResponse(
            bookId1, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
            price, discountRate, salePrice, "책 설명", "책 내용", "1234567890123",
            true, 0, 10, 100L, tagNames, contributorsByRole);

        CustomerCartResponse response1 = new CustomerCartResponse(bookResponse1, 1);

        Long bookId2 = 2L;
        BookResponse bookResponse2 = new BookResponse(
            bookId2, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
            price, discountRate, salePrice, "책 설명", "책 내용", "1234567890123",
            true, 0, 10, 100L, tagNames, contributorsByRole);

        CustomerCartResponse response2 = new CustomerCartResponse(bookResponse2, 2);
        when(customerCartService.getCustomerCartList(anyString())).thenReturn(List.of(response1, response2));

        // when then
        mockMvc.perform(get("/api/customer/cart")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

    }

    @DisplayName("비회원 장바구니를 삭제한다")
    @Test
    void removeCustomerCart() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        // when then
        mockMvc.perform(delete("/api/customer/cart")
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @DisplayName("비회원 장바구니에서 특정 아이템을 삭제한다")
    @Test
    void removeCustomerCartItem() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        //when then
        mockMvc.perform(delete("/api/customer/cart/{bookId}", 1L)
                        .session(session))
                .andExpect(status().isNoContent());
    }

}