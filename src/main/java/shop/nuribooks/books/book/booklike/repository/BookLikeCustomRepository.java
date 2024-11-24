package shop.nuribooks.books.book.booklike.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;

public interface BookLikeCustomRepository {
	Page<BookLikeResponse> findLikedBooks(Long memberId, Pageable pageable);
}
