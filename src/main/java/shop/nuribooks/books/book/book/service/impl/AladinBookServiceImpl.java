package shop.nuribooks.books.book.book.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.AladinBookService;

@RequiredArgsConstructor
@Service
public class AladinBookServiceImpl implements AladinBookService {
	private final BookRepository bookRepository;

	@Value("${aladin.api.key}")
	private String ttbKey;
}
