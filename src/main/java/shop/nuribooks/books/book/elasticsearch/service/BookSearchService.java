package shop.nuribooks.books.book.elasticsearch.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;
import shop.nuribooks.books.book.elasticsearch.enums.SortType;

public interface BookSearchService {
	Page<BookDocument> searchBooks(String keyword, SearchType searchType, SortType sortType, Pageable pageable) throws
		IOException;
}
