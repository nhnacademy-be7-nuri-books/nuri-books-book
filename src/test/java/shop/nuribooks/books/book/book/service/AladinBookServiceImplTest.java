package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.service.impl.AladinBookServiceImpl;
import shop.nuribooks.books.book.client.AladinFeignClient;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.exception.book.AladinApiException;

@ExtendWith(MockitoExtension.class)
public class AladinBookServiceImplTest {
	@InjectMocks
	private AladinBookServiceImpl aladinBookService;

	@Mock
	private AladinFeignClient aladinFeignClient;

	@BeforeEach
	public void setUp() {
		ReflectionTestUtils.setField(aladinBookService, "ttbKey", "ttbljb06271100001");
	}

	@Test
	@DisplayName("알라딘 api를 이용한 도서목록 조회")
	public void getNewBooks() {
		AladinBookListResponse response = mock(AladinBookListResponse.class);
		List<AladinBookListItemResponse> itemResponses = List.of(
			new AladinBookListItemResponse("제목", "작가 (지은이)", "2024-11-21", "설명", "0000000000000",
				BigDecimal.valueOf(10000), "정상판매", "커버", "출판사", "국내>소설"),
			new AladinBookListItemResponse("제목2", "작가 (지은이)", "2024-11-21", "설명", "0000000000001",
				BigDecimal.valueOf(10000), "정상판매", "커버", "출판사", "국내>소설")
		);
		when(response.item()).thenReturn(itemResponses);
		when(aladinFeignClient.getNewBooks(
			eq("ttbljb06271100001"),
			eq("NEW"),
			eq(10),
			eq(1),
			eq("Book"),
			eq("JS"),
			eq("20131101")
		)).thenReturn(response);

		List<AladinBookListItemResponse> result = aladinBookService.getNewBooks("NEW", "Book", 10);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("제목", result.getFirst().title());

		verify(aladinFeignClient, times(1)).getNewBooks(anyString(), anyString(), anyInt(), anyInt(), anyString(),
			anyString(), anyString());
	}

	@Test
	@DisplayName("알라딘 api 이용 도서목록 조회실패 예외")
	public void getNewBooksException() {
		when(aladinFeignClient.getNewBooks(
			anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString(), anyString()
		)).thenThrow(new RuntimeException("AladinFeignClient Exception"));

		AladinApiException aladinApiException = assertThrows(AladinApiException.class,
			() -> aladinBookService.getNewBooks("NEW", "Book", 10));
		assertEquals("Failed to retrieve new books from Aladin", aladinApiException.getMessage());

		verify(aladinFeignClient, times(1)).getNewBooks(
			eq("ttbljb06271100001"),
			eq("NEW"),
			eq(10),
			eq(1),
			eq("Book"),
			eq("JS"),
			eq("20131101")
		);
	}

	@Test
	@DisplayName("알라딘 api를 이용해 isbn으로 특정 도서 조회")
	public void getBookByIsbnWithAladin() {
		AladinBookListResponse response = mock(AladinBookListResponse.class);
		AladinBookListItemResponse itemResponse = new AladinBookListItemResponse(
			"제목",
			"작가 (지은이)",
			"2024-11-22",
			"설명",
			"0000000000000",
			BigDecimal.valueOf(10000),
			"정상판매",
			"커버",
			"출판사",
			"국내>소설"
		);

		when(response.item()).thenReturn(List.of(itemResponse));
		when(aladinFeignClient.getBookDetails(
			eq("ttbljb06271100001"),
			eq("ISBN"),
			eq("0000000000000"),
			eq("Big"),
			eq("JS"),
			eq("20131101")
		)).thenReturn(response);

		AladinBookListItemResponse result = aladinBookService.getBookByIsbnWithAladin("0000000000000");

		assertNotNull(result);
		assertEquals("제목", result.title());
		assertEquals("작가 (지은이)", result.author());
		assertEquals("2024-11-22", result.pubDate());
		assertEquals("설명", result.description());
		assertEquals(BigDecimal.valueOf(10000), result.priceStandard());
		assertEquals("정상판매", result.stockStatus());
		assertEquals("커버", result.cover());
		assertEquals("출판사", result.publisher());
		assertEquals("국내>소설", result.categoryName());

		verify(aladinFeignClient, times(1)).getBookDetails(
			eq("ttbljb06271100001"),
			eq("ISBN"),
			eq("0000000000000"),
			eq("Big"),
			eq("JS"),
			eq("20131101")
		);
	}

	@Test
	@DisplayName("알라딘 api를 이용해 isbn으로 특정 도서 조회 실패")
	public void getBookByIsbnWithAladinException() {
		when(aladinFeignClient.getBookDetails(
			anyString(),
			anyString(),
			anyString(),
			anyString(),
			anyString(),
			anyString()
		)).thenThrow(new RuntimeException("AladinFeignClient Exception"));

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
			() -> aladinBookService.getBookByIsbnWithAladin("0000000000000"));
		assertEquals("도서를 찾을 수 없습니다.", exception.getMessage());

		verify(aladinFeignClient, times(1)).getBookDetails(
			eq("ttbljb06271100001"),
			eq("ISBN"),
			eq("0000000000000"),
			eq("Big"),
			eq("JS"),
			eq("20131101")
		);
	}
}
