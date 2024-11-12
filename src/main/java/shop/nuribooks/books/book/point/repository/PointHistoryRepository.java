package shop.nuribooks.books.book.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, PointHistoryCustomRepository {
}
