package shop.nuribooks.books.book.elasticsearch.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;
import shop.nuribooks.books.book.elasticsearch.enums.SortType;
import shop.nuribooks.books.book.elasticsearch.service.BookSearchService;

@RestController
@RequestMapping("/api/books/search")
public class BookSearchController {

	private final BookSearchService bookSearchService;

	public BookSearchController(BookSearchService bookSearchService) {
		this.bookSearchService = bookSearchService;
	}

	@GetMapping
	public Page<BookDocument> searchBooks(
		@RequestParam("keyword") String keyword,
		@RequestParam(value = "category_id", required = false) Long categoryId,
		@RequestParam(value = "search_type", required = false, defaultValue = "ALL") SearchType searchType,
		@RequestParam(value = "sort_type", required = false, defaultValue = "ACCURACY") SortType sortType,
		Pageable pageable
	) throws IOException {
		return bookSearchService.searchBooks(keyword, categoryId, searchType, sortType, pageable);
	}
}
