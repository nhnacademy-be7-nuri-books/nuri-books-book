package shop.nuribooks.books.book.booklike.service;

import java.util.List;

import shop.nuribooks.books.book.booklike.dto.BookLikeResponse;

public interface BookLikeService {
	void addLike(Long memberId, Long bookId);

	void removeLike(Long memberId, Long bookId);

	List<BookLikeResponse> getLikedBooks(Long memberId);
}
