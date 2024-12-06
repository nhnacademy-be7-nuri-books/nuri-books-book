package shop.nuribooks.books.payment.payment.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.message.ResponseMessage;
import shop.nuribooks.books.exception.order.OrderNotFoundException;
import shop.nuribooks.books.exception.payment.FailedPaymentCancelException;
import shop.nuribooks.books.exception.payment.PaymentNotFoundException;
import shop.nuribooks.books.inventory.message.InventoryUpdateMessage;
import shop.nuribooks.books.member.customer.entity.Customer;
import shop.nuribooks.books.member.grade.entity.Grade;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.order.repository.OrderRepository;
import shop.nuribooks.books.order.orderdetail.entity.OrderDetail;
import shop.nuribooks.books.order.orderdetail.repository.OrderDetailRepository;
import shop.nuribooks.books.payment.payment.dto.PaymentSuccessRequest;
import shop.nuribooks.books.payment.payment.entity.Payment;
import shop.nuribooks.books.payment.payment.entity.PaymentCancel;
import shop.nuribooks.books.payment.payment.entity.PaymentMethod;
import shop.nuribooks.books.payment.payment.entity.PaymentState;
import shop.nuribooks.books.payment.payment.repository.PaymentCancelRepository;
import shop.nuribooks.books.payment.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
	@InjectMocks
	PaymentServiceImpl paymentService;

	@Mock
	MemberRepository memberRepository;
	@Mock
	OrderRepository orderRepository;
	@Mock
	PaymentRepository paymentRepository;
	@Mock
	OrderDetailRepository orderDetailRepository;
	@Mock
	PaymentCancelRepository paymentCancelRepository;
	@Mock
	RabbitTemplate rabbitTemplate;
	@Mock
	RestTemplate restTemplate;
	@Mock
	ObjectMapper objectMapper;
	@Mock
	ApplicationEventPublisher publisher;

	private PaymentSuccessRequest paymentSuccessRequest;
	private Order order;
	private List<OrderDetail> orderDetails;
	private Payment payment;
	private Member member;

	@BeforeEach
	void setUp() {
		Grade grade = TestUtils.creategrade();

		Customer customer = TestUtils.createCustomer();
		TestUtils.setIdForEntity(customer, 1L);

		member = TestUtils.createMember(customer, grade);
		TestUtils.setIdForEntity(member, 1L);

		order = TestUtils.createOrder(customer);
		TestUtils.setIdForEntity(order, 1L);

		orderDetails = new LinkedList<>();
		Publisher pub = TestUtils.createPublisher();
		for (long i = 1; i <= 3; i++) {
			Book book = TestUtils.createBook(pub);
			TestUtils.setIdForEntity(book, i);

			OrderDetail orderDetail = TestUtils.createOrderDetail(order, book);
			TestUtils.setIdForEntity(orderDetail, i);
			orderDetails.add(orderDetail);
		}

		payment = TestUtils.createPayment(order);
		paymentSuccessRequest = new PaymentSuccessRequest(PaymentState.DONE.toString(), "tmpTenChar" + order.getId(),
			"payment-key",
			LocalDateTime.now(), PaymentMethod.CARD.getKorName(), LocalDateTime.now(), 100000L);
	}

	@Test
	void donePayment_success() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
		when(paymentRepository.save(any())).thenReturn(payment);
		when(orderDetailRepository.findAllByOrderId(order.getId())).thenReturn(orderDetails);
		when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

		ResponseMessage responseMessage = paymentService.donePayment(paymentSuccessRequest);

		Assertions.assertEquals(HttpStatus.CREATED.value(), responseMessage.statusCode());
		Assertions.assertEquals("성공", responseMessage.message());
	}

	@Test
	void donePaymentCustomer_success() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
		when(paymentRepository.save(any())).thenReturn(payment);
		when(orderDetailRepository.findAllByOrderId(order.getId())).thenReturn(orderDetails);
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

		ResponseMessage responseMessage = paymentService.donePayment(paymentSuccessRequest);

		Assertions.assertEquals(HttpStatus.CREATED.value(), responseMessage.statusCode());
		Assertions.assertEquals("성공", responseMessage.message());

	}

	@Test
	void donePayment_fail() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(OrderNotFoundException.class, () -> paymentService.donePayment(paymentSuccessRequest));
	}

	// 재고 처리 관련 예외 처리 테스트 수정 필요.
	@Test
	void donePaymentCustomer_success_but_somethingWrong() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
		when(paymentRepository.save(any())).thenReturn(payment);
		when(orderDetailRepository.findAllByOrderId(order.getId())).thenReturn(orderDetails);
		when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());
		doThrow(AmqpRejectAndDontRequeueException.class).when(rabbitTemplate)
			.convertAndSend(any(String.class), any(String.class), any(InventoryUpdateMessage.class));
		ResponseMessage responseMessage = paymentService.donePayment(paymentSuccessRequest);

		Assertions.assertEquals(HttpStatus.CREATED.value(), responseMessage.statusCode());
		Assertions.assertEquals("성공", responseMessage.message());
	}

	@Test
	void cancelPayment_success() throws Exception {
		// Given
		String reason = "그냥";
		String testPaymentKey = "testPaymentKey";
		String cancelUrl = "https://api.tosspayments.com/v1/payments/" + testPaymentKey + "/cancel";

		// Mock Payment
		when(paymentRepository.findByOrder(any())).thenReturn(Optional.of(payment));

		// Mock RestTemplate response
		String jsonResponse = """
			{
			  "cancels": [
			    {
			      "transactionKey": "testTransactionKey",
			      "cancelReason": "그냥",
			      "canceledAt": "2023-01-01T12:00:00+09:00"
			    }
			  ]
			}
			""";

		ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(mockResponseEntity);
		ObjectMapper om = new ObjectMapper();
		when(objectMapper.readTree(anyString())).thenReturn(om.readTree(jsonResponse));

		// Mock order details
		when(orderDetailRepository.findAllByOrderId(order.getId())).thenReturn(orderDetails);

		// When
		ResponseMessage responseMessage = paymentService.cancelPayment(order, reason);

		// Then
		Assertions.assertEquals(200, responseMessage.statusCode());
		Assertions.assertEquals("주문 취소 성공", responseMessage.message());

		// Verify interactions
		verify(paymentRepository, times(1)).findByOrder(eq(order));
		verify(paymentCancelRepository, times(1)).save(any(PaymentCancel.class));
		verify(orderDetailRepository, times(1)).findAllByOrderId(eq(order.getId()));
		verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(String.class));
	}

	@Test
	void cancelPayment_failure() throws JsonProcessingException {
		// Given
		String reason = "그냥";

		when(paymentRepository.findByOrder(any())).thenReturn(Optional.of(payment));

		// Mock RestTemplate failure response
		String jsonResponse = """
			{
			  "message": "결제 취소 실패",
			  "code": "PAYMENT_CANCEL_FAILED"
			}
			""";

		ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
		when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(mockResponseEntity);

		// When & Then
		Assertions.assertThrows(FailedPaymentCancelException.class, () -> paymentService.cancelPayment(order, reason));

		// Verify interactions
		verify(paymentRepository, times(1)).findByOrder(eq(order));
		verify(paymentCancelRepository, never()).save(any(PaymentCancel.class));
		verify(orderDetailRepository, never()).findAllByOrderId(eq(order.getId()));
		verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(String.class));
	}

	@Test
	void cancelPayment_failureJsonException() throws JsonProcessingException {
		// Given
		String reason = "그냥";

		when(paymentRepository.findByOrder(any())).thenReturn(Optional.of(payment));

		// Mock RestTemplate failure response
		String jsonResponse = """
			{
			  "message": "결제 취소 실패",
			  "code": "PAYMENT_CANCEL_FAILED"
			}
			""";

		ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(mockResponseEntity);
		when(objectMapper.readTree(anyString())).thenThrow(JsonProcessingException.class);

		// When & Then
		Assertions.assertThrows(FailedPaymentCancelException.class, () -> paymentService.cancelPayment(order, reason));

		// Verify interactions
		verify(paymentRepository, times(1)).findByOrder(eq(order));
		verify(paymentCancelRepository, never()).save(any(PaymentCancel.class));
		verify(orderDetailRepository, never()).findAllByOrderId(eq(order.getId()));
		verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(String.class));
	}

	@Test
	void cancelPayment_failureJsonToStringException() throws JsonProcessingException {
		// Given
		String reason = "그냥";

		when(paymentRepository.findByOrder(any())).thenReturn(Optional.of(payment));

		when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

		// When & Then
		Assertions.assertThrows(FailedPaymentCancelException.class, () -> paymentService.cancelPayment(order, reason));

		// Verify interactions
		verify(paymentRepository, times(1)).findByOrder(eq(order));
	}

	@Test
	void cancelPayment_failureNoPayment() {
		// Given
		String reason = "그냥";

		when(paymentRepository.findByOrder(any())).thenReturn(Optional.empty());

		// When & Then
		Assertions.assertThrows(PaymentNotFoundException.class, () -> paymentService.cancelPayment(order, reason));
	}

}
