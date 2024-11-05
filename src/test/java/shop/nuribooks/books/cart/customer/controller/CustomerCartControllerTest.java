package shop.nuribooks.books.cart.customer.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
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
    void registerAddress() throws Exception {
        // given
        CustomerCartAddRequest request = new CustomerCartAddRequest("sessionId", 1L, 1);

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

        String sessionId = "sessionId";
        Long bookId1 = 1L;
        BookResponse bookResponse1 = new BookResponse(
                bookId1, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
                BigDecimal.valueOf(10000), 10, "책 설명", "책 내용", "1234567890123",
                true, 0, 10, 100L);

        CustomerCartResponse response1 = new CustomerCartResponse(bookResponse1, 1);

        Long bookId2 = 2L;
        BookResponse bookResponse2 = new BookResponse(
                bookId2, null, "정상", "책 제목", "thumbnail.jpg", null, LocalDate.now(),
                BigDecimal.valueOf(10000), 10, "책 설명", "책 내용", "1234567890123",
                true, 0, 10, 100L);

        CustomerCartResponse response2 = new CustomerCartResponse(bookResponse2, 2);
        when(customerCartService.getCustomerCartList(anyString())).thenReturn(List.of(response1, response2));

        // when then
        mockMvc.perform(get("/api/customer/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

    }

    @DisplayName("비회원 장바구니를 삭제한다")
    @Test
    void removeCustomerCart() throws Exception {
        // given when then
        mockMvc.perform(delete("/api/customer/cart"))
                .andExpect(status().isNoContent());
    }

    @DisplayName("비회원 장바구니에서 특정 아이템을 삭제한다")
    @Test
    void removeCustomerCartItem() throws Exception {
        // given when then
        mockMvc.perform(delete("/api/customer/cart/{bookId}", 1L))
                .andExpect(status().isNoContent());
    }

}