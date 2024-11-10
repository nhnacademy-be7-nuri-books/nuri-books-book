package shop.nuribooks.books.book.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageCustomRepository {
}
