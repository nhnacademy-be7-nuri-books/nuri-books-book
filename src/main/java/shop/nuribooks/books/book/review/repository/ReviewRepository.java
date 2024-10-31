package shop.nuribooks.books.book.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	//도서 id로 리뷰 목록 조회

	// 도서 id로 총 별점 조회

	// 유저 id로 리뷰 조회
}
