package shop.nuribooks.books.book.point.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.entity.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
	List<PointPolicyResponse> findAllByDeletedAtIsNull();

	PointPolicyResponse findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(String name);

	Boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}
