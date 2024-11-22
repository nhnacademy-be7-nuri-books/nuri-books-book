package shop.nuribooks.books.order.wrapping.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;
import shop.nuribooks.books.order.wrapping.exception.WrappingPaperNotFoundException;
import shop.nuribooks.books.order.wrapping.repository.WrappingPaperRepository;
import shop.nuribooks.books.order.wrapping.service.WrappingPaperService;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class WrappingPaperServiceImpl implements WrappingPaperService {
	private final WrappingPaperRepository wrappingPaperRepository;

	@Override
	public Page<WrappingPaperResponse> getWrappingPaperResponse(Pageable pageable) {
		return wrappingPaperRepository.findAll(pageable).map(WrappingPaper::toResponseDto);
	}

	@Transactional
	@Override
	public WrappingPaper registerWrappingPaper(WrappingPaperRequest wrappingPaperRequest) {
		return wrappingPaperRepository.save(wrappingPaperRequest.toEntity());
	}

	@Transactional
	@Override
	public WrappingPaper updateWrappingPaper(Long id, WrappingPaperRequest wrappingPaperRequest) {
		WrappingPaper wrappingPaper = wrappingPaperRepository.findById(id)
			.orElseThrow(WrappingPaperNotFoundException::new);
		wrappingPaper.update(wrappingPaperRequest);
		return wrappingPaper;
	}

	@Transactional
	@Override
	public void deleteWrappingPaper(Long id) {
		WrappingPaper wrappingPaper = wrappingPaperRepository.findById(id)
			.orElseThrow(WrappingPaperNotFoundException::new);
		wrappingPaperRepository.delete(wrappingPaper);
	}
}
