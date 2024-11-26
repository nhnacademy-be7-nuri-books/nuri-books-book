package shop.nuribooks.books.order.wrapping.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;

/**
 * 포장지 서비스 인터페이스
 *
 * @author nuri
 */
public interface WrappingPaperService {
	Page<WrappingPaperResponse> getWrappingPaperResponse(Pageable pageable);

	WrappingPaper registerWrappingPaper(WrappingPaperRequest wrappingPaperRequest);

	WrappingPaper updateWrappingPaper(Long id, WrappingPaperRequest wrappingPaperRequest);

	void deleteWrappingPaper(Long id);

	List<WrappingPaperResponse> getAllWrappingPaper();

	WrappingPaper getWrappingPaper(Long wrappingId);
}
