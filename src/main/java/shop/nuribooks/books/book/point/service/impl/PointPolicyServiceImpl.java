package shop.nuribooks.books.book.point.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointPolicyService;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {
	private final PointPolicyRepository pointPolicyRepository;

	@Override
	public PointPolicy registerPointPolicy(PointPolicyRequest pointPolicyRequest) {
		if (this.pointPolicyRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(pointPolicyRequest.name())) {
			throw new ResourceAlreadyExistException("같은 이름의 포인트 정책이 이미 존재합니다.");
		}

		PointPolicy pointPolicy = pointPolicyRequest.toEntity();
		return this.pointPolicyRepository.save(pointPolicy);
	}
}
