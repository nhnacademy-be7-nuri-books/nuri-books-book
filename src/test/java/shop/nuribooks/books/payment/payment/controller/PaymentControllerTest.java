package shop.nuribooks.books.payment.payment.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;
import shop.nuribooks.books.payment.payment.entity.PaymentMethod;
import shop.nuribooks.books.payment.payment.entity.PaymentState;
import shop.nuribooks.books.payment.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PaymentService paymentService;

	@Test
	void donePayment() throws Exception {
		Customer customer = TestUtils.createCustomer();
		TestUtils.setIdForEntity(customer, 1L);

		Order order = TestUtils.createOrder(customer);
		TestUtils.setIdForEntity(order, 1L);

		PaymentSuccessRequest paymentSuccessRequest = new PaymentSuccessRequest(PaymentState.DONE.toString(),
			"tmpTenChar" + order.getId(),
			"payment-key",
			LocalDateTime.now(), PaymentMethod.CARD.getKorName(), LocalDateTime.now(), 100000L);

		when(paymentService.donePayment(any())).thenReturn(new ResponseMessage(201, "성공"));
		mockMvc.perform(post("/api/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentSuccessRequest)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.statusCode").value(201))
			.andExpect(jsonPath("$.message").value("성공"))
		;
	}
}
