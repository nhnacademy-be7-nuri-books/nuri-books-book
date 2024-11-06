package shop.nuribooks.books.book.book.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;

public interface AladinBookService {
	List<AladinBookListItemResponse> getNewBooks();
	AladinBookListItemResponse getBookByIsbnWithAladin(String isbn);
	void saveBook(AladinBookSaveRequest bookRegisterRequest);
}
