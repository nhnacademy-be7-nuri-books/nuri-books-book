package shop.nuribooks.books.book.elasticsearch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;

public interface BookSearchService {
	Page<BookDocument> searchBooks(String keyword, SearchType searchType, Pageable pageable);
}
