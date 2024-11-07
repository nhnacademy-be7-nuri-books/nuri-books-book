package shop.nuribooks.books.book.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.entity.Book;

public interface BookRepositoryCustom {
	Page<Book> findAllWithPublisher(Pageable pageable);
}
