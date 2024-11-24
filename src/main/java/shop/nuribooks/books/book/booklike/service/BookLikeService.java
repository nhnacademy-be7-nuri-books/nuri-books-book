package shop.nuribooks.books.book.booklike.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;

public interface BookLikeService {
	void addLike(Long memberId, Long bookId);

	void removeLike(Long memberId, Long bookId);

	Page<BookLikeResponse> getLikedBooks(Long memberId, Pageable pageable);

	boolean isBookLikedByMember(Long memberId, Long bookId);
}
