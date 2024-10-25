package shop.nuribooks.books.service.book;

import shop.nuribooks.books.dto.book.BookRegisterRequest;
import shop.nuribooks.books.dto.book.BookRegisterResponse;

public interface BookService {
	BookRegisterResponse registerBook(BookRegisterRequest reqDto);
}
