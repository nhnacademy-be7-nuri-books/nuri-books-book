package shop.nuribooks.books.order.wrapping.service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperRequest;
import shop.nuribooks.books.order.wrapping.dto.WrappingPaperResponse;
import shop.nuribooks.books.order.wrapping.entity.WrappingPaper;
import shop.nuribooks.books.order.wrapping.exception.WrappingPaperNotFoundException;
import shop.nuribooks.books.order.wrapping.repository.WrappingPaperRepository;
import shop.nuribooks.books.order.wrapping.service.impl.WrappingPaperServiceImpl;

@ExtendWith(MockitoExtension.class)
class WrappingServiceTest {
	@InjectMocks
	private WrappingPaperServiceImpl wrappingPaperService;

	@Mock
	private WrappingPaperRepository wrappingPaperRepository;

	private WrappingPaper wrappingPaper;
	private WrappingPaperResponse wrappingPaperResponse;

	@BeforeEach
	void setUp() {
		wrappingPaper = TestUtils.createWrappingPaper();
		TestUtils.setIdForEntity(wrappingPaper, 1L);
		wrappingPaperResponse = new WrappingPaperResponse(
			wrappingPaper.getId(),
			wrappingPaper.getTitle(),
			wrappingPaper.getImageUrl(),
			wrappingPaper.getWrappingPrice());
	}

	@Test
	void getWrappingPaperResponse() {
		Pageable pageable = PageRequest.of(1, 1);
		when(wrappingPaperRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(wrappingPaper), pageable, 1));

		Page<WrappingPaperResponse> pages = wrappingPaperService.getWrappingPaperResponse(pageable);
		Assertions.assertEquals(1, pages.getSize());
		Assertions.assertEquals(1, pages.getNumber());
		Assertions.assertIterableEquals(List.of(wrappingPaperResponse), pages.getContent());
	}

	@Test
	void getAllWrappingPaperResponse() {
		when(wrappingPaperRepository.findAll()).thenReturn(List.of(wrappingPaper));

		List<WrappingPaperResponse> res = wrappingPaperService.getAllWrappingPaper();
		Assertions.assertIterableEquals(List.of(wrappingPaperResponse), res);
	}

	@Test
	void getWrappingPaper() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.of(wrappingPaper));

		Assertions.assertEquals(wrappingPaper, wrappingPaperService.getWrappingPaper(1L));
	}

	@Test
	void getWrappingPaperFailed() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(WrappingPaperNotFoundException.class,
			() -> wrappingPaperService.getWrappingPaper(111L));
	}

	@Test
	void registerWrappingPaper() {
		when(wrappingPaperRepository.save(any())).thenReturn(wrappingPaper);
		WrappingPaperRequest wrappingPaperRequest = new WrappingPaperRequest("이름임당", "image.com", BigDecimal.TEN);

		Assertions.assertEquals(wrappingPaper, wrappingPaperService.registerWrappingPaper(wrappingPaperRequest));
	}

	@Test
	void updateWrappingPaper() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.of(wrappingPaper));
		WrappingPaperRequest wrappingPaperRequest = new WrappingPaperRequest("이름임당", "image.com", BigDecimal.TEN);

		wrappingPaper.update(wrappingPaperRequest);

		Assertions.assertEquals(wrappingPaper, wrappingPaperService.updateWrappingPaper(1L, wrappingPaperRequest));
	}

	@Test
	void updateWrappingPaperFailed() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.empty());
		WrappingPaperRequest wrappingPaperRequest = new WrappingPaperRequest("이름임당", "image.com", BigDecimal.TEN);

		Assertions.assertThrows(WrappingPaperNotFoundException.class,
			() -> wrappingPaperService.updateWrappingPaper(1L, wrappingPaperRequest));
	}

	@Test
	void deleteTest() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.of(wrappingPaper));
		wrappingPaperService.deleteWrappingPaper(1L);
		verify(wrappingPaperRepository).delete(wrappingPaper);
	}

	@Test
	void deleteTestFail() {
		when(wrappingPaperRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(WrappingPaperNotFoundException.class,
			() -> wrappingPaperService.deleteWrappingPaper(1L));
	}
}
