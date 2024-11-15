package shop.nuribooks.books.book.book.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.book.client.AladinFeignClient;
import shop.nuribooks.books.exception.ResourceNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AladinBookServiceImpl implements AladinBookService {
	private final AladinFeignClient aladinFeignClient;

	@Value("${aladin.api.key}")
	private String ttbKey;

	//도서 리스트 조회 메서드
	@Override
	public List<AladinBookListItemResponse> getNewBooks(String queryType, String searchTarget, int maxResults) {
		try {
			AladinBookListResponse response = aladinFeignClient.getNewBooks(ttbKey, queryType, maxResults, 1, "Book", "JS", "20131101");
			log.info("Received BookList response: {}", response);
			return response.item();
		} catch (Exception ex) {
			log.error("Error: ", ex);
			throw new RuntimeException("Failed to retrieve new books from Aladin");
		}
	}

	@Override
	public AladinBookListItemResponse getBookByIsbnWithAladin(String isbn) {
		try {
			AladinBookListResponse response = aladinFeignClient.getBookDetails(ttbKey, "ISBN", isbn, "JS", "20131101");
			log.info("Received Book response: {}", response);
			return response.item().getFirst();
		} catch (Exception ex) {
			log.error("Error: ", ex);
			throw new ResourceNotFoundException("도서를 찾을 수 없습니다.");
		}
	}
}
