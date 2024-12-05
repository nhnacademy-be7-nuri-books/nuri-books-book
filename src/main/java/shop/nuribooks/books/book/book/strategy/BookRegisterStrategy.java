package shop.nuribooks.books.book.book.strategy;

import shop.nuribooks.books.book.book.dto.request.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;

public interface BookRegisterStrategy {
	void registerCategory(BaseBookRegisterRequest request, Book book);
}
