package shop.nuribooks.books.book.book.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;

public interface AladinBookService {
	List<AladinBookListResponse> getNewBooks();
}
