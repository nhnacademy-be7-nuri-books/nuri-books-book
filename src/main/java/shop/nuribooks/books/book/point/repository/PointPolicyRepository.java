package shop.nuribooks.books.book.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.entity.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
}
