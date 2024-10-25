package shop.nuribooks.books.book.book.service;

import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterResponse;

public interface BookService {
	BookRegisterResponse registerBook(BookRegisterRequest reqDto);
}
