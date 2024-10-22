package shop.nuribooks.books.service.book;

import shop.nuribooks.books.dto.book.BookRegisterReq;
import shop.nuribooks.books.dto.book.BookRegisterRes;

public interface BookService {
	BookRegisterRes registerBook(BookRegisterReq reqDto);
}
