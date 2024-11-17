package shop.nuribooks.books.book.elasticsearch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.publisher.entity.Publisher;

public interface BookSearchService {
	Page<BookDocument> searchBooks(String keyword, Pageable pageable);

	BookDocument saveBook(Book book, Publisher publisher);

	void updateBook(String id, BookDocument updatedDocument);

	void deleteBook(String id);
}
