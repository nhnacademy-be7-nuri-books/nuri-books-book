package shop.nuribooks.books.book.book.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;

public interface AladinBookService {
	List<AladinBookListItemResponse> getNewBooks(String queryType, String searchTarget, int maxResults);
	AladinBookListItemResponse getBookByIsbnWithAladin(String isbn);
}
