package shop.nuribooks.books.book.booktag.service;

import java.util.List;

import shop.nuribooks.books.book.book.dto.response.BookResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;

public interface BookTagService {
	BookTagRegisterResponse registerTagToBook(BookTagRequest request);

	BookTagGetResponse getBookTag(Long bookId);

	List<BookResponse> getBooksByTagId(Long tagId);

	void deleteBookTag(Long bookTagId);

	void registerTagToBook(Long bookId, List<Long> tagIds);

	void deleteBookTagIds(Long bookId);
}
