package shop.nuribooks.books.book.book.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;

public interface AladinBookService {
	List<AladinBookListItemResponse> getNewBooks();
}
