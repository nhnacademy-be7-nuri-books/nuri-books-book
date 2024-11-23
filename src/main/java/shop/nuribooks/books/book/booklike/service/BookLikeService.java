package shop.nuribooks.books.book.booklike.service;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;
import shop.nuribooks.books.common.message.PagedResponse;

public interface BookLikeService {
	void addLike(Long memberId, Long bookId);

	void removeLike(Long memberId, Long bookId);

	PagedResponse<BookLikeResponse> getLikedBooks(Long memberId, Pageable pageable);
}
