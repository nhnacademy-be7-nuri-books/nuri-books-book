package shop.nuribooks.books.book.book.service;

import java.util.List;

import shop.nuribooks.books.book.book.entity.Book;

public interface CategoryRegisterService {
	void registerAladinCategories(String categoryName, Book book);

	void registerPersonallyCategories(List<Long> categoryIds, Book book);
}
