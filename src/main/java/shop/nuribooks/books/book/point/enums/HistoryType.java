package shop.nuribooks.books.book.point.enums;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import reactor.function.Function4;
import shop.nuribooks.books.book.point.dto.request.PointHistoryPeriodRequest;
import shop.nuribooks.books.book.point.dto.response.PointHistoryResponse;
import shop.nuribooks.books.book.point.service.PointHistoryService;

public enum HistoryType {
	// 각 조건별로 실행해야 할 함수 실행.
	ALL((service, memberId, pageable, req) -> service.getPointHistories(memberId, pageable, req)),
	USED((service, memberId, pageable, req) -> service.getUsedPointHistories(memberId, pageable, req)),
	SAVED((service, memberId, pageable, req) -> service.getEarnedPointHistories(memberId, pageable, req));

	private Function4<PointHistoryService, Long, Pageable, PointHistoryPeriodRequest, Page<PointHistoryResponse>> func;

	HistoryType(
		Function4<PointHistoryService, Long, Pageable, PointHistoryPeriodRequest, Page<PointHistoryResponse>> func) {
		this.func = func;
	}

	public static HistoryType convert(String str) {
		HistoryType type;
		try {
			type = HistoryType.valueOf(str.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			type = HistoryType.ALL;
		}
		return type;
	}

	// 람다함수 실제 적용
	public Page<PointHistoryResponse> apply(
		PointHistoryService service,
		Long memberId,
		Pageable pageable,
		PointHistoryPeriodRequest req) {
		return func.apply(service, memberId, pageable, req);
	}

}
