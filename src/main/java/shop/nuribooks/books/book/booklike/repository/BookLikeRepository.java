package shop.nuribooks.books.book.booklike.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.booklike.entity.BookLike;
import shop.nuribooks.books.book.booklike.entity.BookLikeId;

public interface BookLikeRepository extends JpaRepository<BookLike, BookLikeId>, BookLikeCustomRepository {
	boolean existsByMemberIdAndBookId(Long memberId, Long bookId);
}
