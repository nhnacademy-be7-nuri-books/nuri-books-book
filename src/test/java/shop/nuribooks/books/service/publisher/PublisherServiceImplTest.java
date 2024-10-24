package shop.nuribooks.books.service.publisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import shop.nuribooks.books.dto.publisher.PublisherRequest;
import shop.nuribooks.books.dto.publisher.PublisherResponse;
import shop.nuribooks.books.entity.book.Publisher;
import shop.nuribooks.books.exception.publisher.PublisherAlreadyExistsException;
import shop.nuribooks.books.repository.publisher.PublisherRepository;

@SpringBootTest
class PublisherServiceImplTest {

	@Mock
	private PublisherRepository publisherRepository;

	@InjectMocks
	private PublisherServiceImpl publisherService;

	@DisplayName("출판사 등록 성공")
	@Test
	void registerPublisher() {
		//given
		PublisherRequest request = registerRequest();
		Publisher savedPublisher = savedPublisher();

		when(publisherRepository.existsByName(request.name())).thenReturn(false);
		when(publisherRepository.save(any(Publisher.class))).thenReturn(savedPublisher);

		// when
		PublisherResponse response = publisherService.registerPublisher(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("publisher1");

		// 메서드 호출 여부 검증
		verify(publisherRepository).existsByName(request.name());
		verify(publisherRepository).save(any(Publisher.class));
	}

	@DisplayName("출판사 등록 실패 - 중복")
	@Test
	void failed_registerPublisher() {
		// given
		PublisherRequest request = registerRequest();

		when(publisherRepository.existsByName(request.name())).thenReturn(true);

		//then
		assertThatThrownBy(() -> publisherService.registerPublisher(request))
			.isInstanceOf(PublisherAlreadyExistsException.class)
			.hasMessageContaining("출판사가 이미 등록되어 있습니다.");

		// 메서드 호출 여부 검증
		verify(publisherRepository).existsByName(request.name());
		verify(publisherRepository, never()).save(any(Publisher.class));
	}

	private PublisherRequest registerRequest() {
		return PublisherRequest.builder()
			.name("publisher1")
			.build();
	}

	private Publisher savedPublisher() {
		return Publisher.builder()
			.name("publisher1")
			.build();
	}
}