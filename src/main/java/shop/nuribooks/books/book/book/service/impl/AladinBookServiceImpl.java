package shop.nuribooks.books.book.book.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.book.client.AladinFeginClient;

@RequiredArgsConstructor
@Service
public class AladinBookServiceImpl implements AladinBookService {
	private final AladinFeginClient aladinFeginClient;

	@Value("${aladin.api.key}")
	private String ttbKey;

	//도서 리스트 조회 메서드
	//TODO: 추후 파라미터 추가 예정
	@Override
	public List<AladinBookListResponse> getNewBooks() {
		return aladinFeginClient.getNewBooks(ttbKey, "ItemNewAll", 10, 1, "Book", "JS");
	}
}
