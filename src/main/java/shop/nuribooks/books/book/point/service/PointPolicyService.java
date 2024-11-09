package shop.nuribooks.books.book.point.service;

import java.util.List;

import shop.nuribooks.books.book.point.dto.request.PointPolicyRequest;
import shop.nuribooks.books.book.point.dto.response.PointPolicyResponse;
import shop.nuribooks.books.book.point.entity.PointPolicy;

public interface PointPolicyService {
	/**
	 * 포인트 정책 목록 가져오기
	 * @return
	 */
	List<PointPolicyResponse> getPointPolicyResponses();

	/**
	 * 포인트 정책 등록
	 * @param pointPolicyRequest
	 * @return
	 */
	PointPolicy registerPointPolicy(PointPolicyRequest pointPolicyRequest);

	/**
	 * 포인트 정책 업데이트
	 * @param id
	 * @param pointPolicyRequest
	 * @return
	 */
	PointPolicy updatePointPolicy(long id, PointPolicyRequest pointPolicyRequest);

	/**
	 * 포인트 삭제
	 * @param id
	 */
	void deletePointPolicy(long id);
}
