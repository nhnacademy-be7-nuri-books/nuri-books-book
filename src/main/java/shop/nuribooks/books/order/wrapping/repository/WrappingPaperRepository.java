package shop.nuribooks.books.order.wrapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;

public interface WrappingPaperRepository extends JpaRepository<WrappingPaper, Long> {
}
