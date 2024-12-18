package shop.nuribooks.books.book.point.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.entity.PointPolicy;
import shop.nuribooks.books.book.point.exception.PointPolicyNotFoundException;
import shop.nuribooks.books.book.point.repository.PointPolicyRepository;
import shop.nuribooks.books.book.point.service.PointPolicyService;
import shop.nuribooks.books.exception.ResourceAlreadyExistException;

@Service
@RequiredArgsConstructor
public class PointPolicyServiceImpl implements PointPolicyService {
	private final PointPolicyRepository pointPolicyRepository;

	/**
	 * 포인트 정책 목록 가져오기
	 *
	 * @return
	 */
	@Override
	public Page<PointPolicyResponse> getPointPolicyResponses(Pageable pageable) {
		return this.pointPolicyRepository.findAllByDeletedAtIsNull(pageable);
	}

	/**
	 * 포인트 정책 생성
	 *
	 * @param pointPolicyRequest
	 * @return
	 */
	@Override
	public PointPolicy registerPointPolicy(PointPolicyRequest pointPolicyRequest) {
		Boolean isExist = this.pointPolicyRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(
			pointPolicyRequest.name());
		if (Boolean.TRUE.equals(isExist)) {
			throw new ResourceAlreadyExistException("같은 이름의 포인트 정책이 이미 존재합니다.");
		}

		PointPolicy pointPolicy = pointPolicyRequest.toEntity();
		return this.pointPolicyRepository.save(pointPolicy);
	}

	/**
	 * 포인트 정책 업데이트
	 *
	 * @param pointPolicyRequest
	 * @return
	 */
	@Transactional
	@Override
	public PointPolicy updatePointPolicy(long id, PointPolicyRequest pointPolicyRequest) {
		PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(id)
			.orElseThrow(PointPolicyNotFoundException::new);

		pointPolicy.update(pointPolicyRequest);
		return pointPolicy;
	}

	/**
	 * 포인트 삭제
	 *
	 * @param id
	 */
	@Transactional
	@Override
	public void deletePointPolicy(long id) {
		PointPolicy pointPolicy = pointPolicyRepository.findPointPolicyByIdAndDeletedAtIsNull(id)
			.orElseThrow(PointPolicyNotFoundException::new);

		pointPolicy.delete();
	}
}
