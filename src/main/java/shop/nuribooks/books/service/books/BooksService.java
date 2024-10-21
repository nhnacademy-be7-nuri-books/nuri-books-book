package shop.nuribooks.books.service.books;

import shop.nuribooks.books.dto.books.BooksRegisterReqDto;
import shop.nuribooks.books.dto.books.BooksRegisterResDto;

public interface BooksService {
	BooksRegisterResDto registerBook(BooksRegisterReqDto reqDto);
}
