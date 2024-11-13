package shop.nuribooks.books.book.point.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.entity.PointPolicy;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
	Page<PointPolicyResponse> findAllByDeletedAtIsNull(Pageable pageable);

	Optional<PointPolicy> findPointPolicyByNameIgnoreCaseAndDeletedAtIsNull(String name);

	Boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

	Optional<PointPolicy> findPointPolicyByIdAndDeletedAtIsNull(long id);
}
