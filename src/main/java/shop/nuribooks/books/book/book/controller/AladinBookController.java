package shop.nuribooks.books.book.book.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.service.AladinBookService;

@RequestMapping("api/books/aladin")
@RequiredArgsConstructor
@RestController
public class AladinBookController {
	private final AladinBookService aladinBookService;

	@GetMapping
	public List<AladinBookListResponse> getAladinBookList() {
		return aladinBookService.getNewBooks();
	}
}
