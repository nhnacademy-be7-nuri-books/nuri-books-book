package shop.nuribooks.books.book.review.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.review.dto.response.ReviewBookResponse;
import shop.nuribooks.books.book.review.dto.response.ReviewMemberResponse;

public interface ReviewCustomRepository {
	// 도서 id로 총 별점 조회
	Double findScoreByBookId(long bookId);

	//도서 id로 리뷰 목록 조회
	List<ReviewMemberResponse> findReviewsByBookId(long bookId, Pageable pageable);

	// 유저 id로 리뷰 조회
	List<ReviewBookResponse> findReviewsByMemberId(long memberId, Pageable pageable);

	// book id로 리뷰 개수
	long countByBookId(long bookId);

	// user id로 리뷰 개수
	long countByMemberId(long memberId);

}
