package shop.nuribooks.books.book.booktag.service;

import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagResponse;

public interface BookTagService {
	BookTagResponse registerTagToBook(BookTagRequest request);

}
