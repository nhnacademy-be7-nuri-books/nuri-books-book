package shop.nuribooks.books.service.book;

import shop.nuribooks.books.dto.book.request.BookRegisterReq;
import shop.nuribooks.books.dto.book.response.BookRegisterRes;

public interface BookService {
	BookRegisterRes registerBook(BookRegisterReq reqDto);
}
