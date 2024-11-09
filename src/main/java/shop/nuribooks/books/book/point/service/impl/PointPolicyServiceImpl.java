package shop.nuribooks.books.book.point.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

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
	public List<PointPolicyResponse> getPointPolicyResponses() {
		return this.pointPolicyRepository.findAllByDeletedAtIsNull();
	}

	/**
	 * 포인트 정책 생성
	 *
	 * @param pointPolicyRequest
	 * @return
	 */
	@Override
	public PointPolicy registerPointPolicy(PointPolicyRequest pointPolicyRequest) {
		if (this.pointPolicyRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(pointPolicyRequest.name())) {
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
	@Override
	public PointPolicy updatePointPolicy(long id, PointPolicyRequest pointPolicyRequest) {
		PointPolicy pointPolicy = pointPolicyRepository.findById(id)
			.orElseThrow(() -> new PointPolicyNotFoundException());

		pointPolicy.update(pointPolicyRequest);

		return pointPolicy;
	}

	/**
	 * 포인트 삭제
	 *
	 * @param id
	 */
	@Override
	public void deletePointPolicy(long id) {
		PointPolicy pointPolicy = pointPolicyRepository.findById(id)
			.orElseThrow(() -> new PointPolicyNotFoundException());

		pointPolicy.delete();
	}
}
