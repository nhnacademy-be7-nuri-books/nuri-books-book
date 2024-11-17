package shop.nuribooks.books.book.elasticsearch.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.service.BookSearchService;

@RestController
public class BookSearchController {

	private final BookSearchService bookSearchService;

	public BookSearchController(BookSearchService bookSearchService) {
		this.bookSearchService = bookSearchService;
	}

	@GetMapping("/books/search")
	public Page<BookDocument> searchBooks(
		@RequestParam("keyword") String keyword,
		Pageable pageable
	) {
		return bookSearchService.searchBooks(keyword, pageable);
	}
}
