package shop.nuribooks.books.book.publisher.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.publisher.dto.PublisherRequest;
import shop.nuribooks.books.book.publisher.dto.PublisherResponse;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.publisher.PublisherAlreadyExistsException;
import shop.nuribooks.books.exception.publisher.PublisherNotFoundException;

@ExtendWith(MockitoExtension.class)
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
		assertThatThrownBy(() -> publisherService.registerPublisher(request)).isInstanceOf(
			PublisherAlreadyExistsException.class).hasMessageContaining("출판사가 이미 등록되어 있습니다.");

		// 메서드 호출 여부 검증
		verify(publisherRepository).existsByName(request.name());
		verify(publisherRepository, never()).save(any(Publisher.class));
	}

	@DisplayName("모든 출판사 조회 성공")
	@Test
	void getAllPublisher() {
		// given
		List<Publisher> publishers = List.of(new Publisher(1L, "publisher1"), new Publisher(2L, "publisher2"));

		Pageable pageable = PageRequest.of(0, 10);
		Page<Publisher> publisherPage = new PageImpl<>(publishers, pageable, publishers.size());

		when(publisherRepository.findAll(pageable)).thenReturn(publisherPage);
		// when
		Page<PublisherResponse> responses = publisherService.getAllPublisher(pageable);

		// then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(2);
		assertThat(responses.getContent().get(0).name()).isEqualTo("publisher1");

		// 메서드 호출 여부 검증
		verify(publisherRepository).findAll(pageable);
	}

	@DisplayName("검색한 출판사 조회 성공")
	@Test
	void getPublisher() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher(publisherId, "publisher1");

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));

		// when
		PublisherResponse response = publisherService.getPublisher(publisherId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(publisherId);

		// 메서드 호출 여부 검증
		verify(publisherRepository).findById(publisherId);
	}

	@DisplayName("검색한 출판사 조회 실패 - 존재하지 않음")
	@Test
	void failed_getPublisher() {
		// given
		Long publisherId = 999L;

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

		// then
		assertThatThrownBy(() -> publisherService.getPublisher(publisherId)).isInstanceOf(
			PublisherNotFoundException.class).hasMessageContaining("출판사가 존재하지 않습니다.");

		verify(publisherRepository).findById(publisherId);

	}

	@DisplayName("검색한 출판사 삭제 성공")
	@Test
	void deletePublisher() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher(publisherId, "publisher1");

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));

		// when
		publisherService.deletePublisher(publisherId);

		// then
		verify(publisherRepository).findById(publisherId);
		verify(publisherRepository).delete(publisher);
	}

	@DisplayName("삭제할 출판사 조회 실패 - 존재하지 않음")
	@Test
	void failed_deletePublisher() {
		// given
		Long publisherId = 999L;

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

		// then
		assertThatThrownBy(() -> publisherService.deletePublisher(publisherId)).isInstanceOf(
			PublisherNotFoundException.class).hasMessageContaining("출판사가 존재하지 않습니다.");

		verify(publisherRepository).findById(publisherId);

	}

	@DisplayName("출판사 수정 성공")
	@Test
	void updatePublisher() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher(publisherId, "publisher1");
		PublisherRequest editRequest = editPublisherRequest();

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));

		// when
		PublisherResponse response = publisherService.updatePublisher(publisherId, editRequest);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("publisher2");
		verify(publisherRepository, times(1)).findById(publisherId);

	}

	@DisplayName("출판사 수정 실패")
	@Test
	void updatePublisher_fail() {
		// given
		Long publisherId = 1L;
		Publisher publisher = new Publisher(publisherId, "publisher1");
		PublisherRequest editRequest = editPublisherRequest();

		// when
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
		when(publisherRepository.existsByName(any())).thenReturn(true);
		// then
		assertThatThrownBy(() -> publisherService.updatePublisher(publisherId, editRequest)
		).isInstanceOf(PublisherAlreadyExistsException.class)
			.hasMessage("출판사가 이미 등록되어 있습니다.");

	}

	@DisplayName("출판사 수정 실패 - 존재하지 않음")
	@Test
	void failed_updatePublisher() {
		// given
		Long publisherId = 999L;
		PublisherRequest editRequest = editPublisherRequest();

		when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> publisherService.updatePublisher(publisherId, editRequest)
		).isInstanceOf(PublisherNotFoundException.class)
			.hasMessage("출판사가 존재하지 않습니다.");

	}

	private PublisherRequest registerRequest() {
		return PublisherRequest.builder().name("publisher1").build();
	}

	private Publisher savedPublisher() {
		return Publisher.builder().name("publisher1")
			.build();
	}

	private PublisherRequest editPublisherRequest() {
		return PublisherRequest.builder().name("publisher2")
			.build();
	}
}