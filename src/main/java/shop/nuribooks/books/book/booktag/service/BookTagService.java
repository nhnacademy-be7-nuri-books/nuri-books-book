package shop.nuribooks.books.book.booktag.service;

import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;

import java.util.List;

public interface BookTagService {
	BookTagRegisterResponse registerTagToBook(BookTagRequest request);

	BookTagGetResponse getBookTag(Long bookId);

	List<BookResponse> getBooksByTagId(Long tagId);

	void deleteBookTag(Long bookTagId);
}
