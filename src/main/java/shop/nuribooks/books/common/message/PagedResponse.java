package shop.nuribooks.books.common.message;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Builder;

@Builder
public record PagedResponse<T>(
	List<T> content, //도서 목록 데이터
	int page, //현재 페이지
	int size, //페이지 크기
	int totalPages, //전체 페이지 수
	long totalElements //전체 요소 수
) {
	public PagedResponse of(List content, Pageable pageable, int totalElement) {
		return PagedResponse.builder().content(content)
			.page(pageable.getPageNumber())
			.size(pageable.getPageSize())
			.totalPages(totalElement / pageable.getPageSize() + totalElement % pageable.getPageSize() == 0 ? 0 : 1)
			.totalElements(totalElements)
			.build();
	}
}
