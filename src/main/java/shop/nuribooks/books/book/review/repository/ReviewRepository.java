package shop.nuribooks.books.book.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
