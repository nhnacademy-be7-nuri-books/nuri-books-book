package shop.nuribooks.books.book.contributor.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.exception.contributor.ContributorNotFoundException;

@SpringBootTest
class ContributorServiceImplTest {

	@Mock
	private ContributorRepository contributorRepository;

	@InjectMocks
	private ContributorServiceImpl contributorService;

	@DisplayName("기여자 등록 성공")
	@Test
	void registerContributor() {
		// given
		Contributor contributor = register();
		ContributorRequest request = ContributorRequest.builder().name("Kim").build();

		// when
		when(contributorRepository.save(any(Contributor.class))).thenReturn(contributor);

		ContributorResponse response = contributorService.registerContributor(request);

		// then
		assertNotNull(response);
		assertThat(response.name()).isEqualTo("Kim");

		verify(contributorRepository, times(1)).save(any(Contributor.class));
	}

	@DisplayName("기여자 수정 성공")
	@Test
	void updateContributor() {
		// given
		Long contributorId = 1L;
		ContributorRequest request = ContributorRequest.builder().name("Lee").build();
		Contributor existingContributor = register();

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.of(existingContributor));
		when(contributorRepository.save(any(Contributor.class))).thenReturn(existingContributor);

		ContributorResponse response = contributorService.updateContributor(contributorId, request);

		// then
		assertNotNull(response);
		assertEquals(contributorId, response.id());
		assertEquals("Lee", response.name());

		verify(contributorRepository, times(1)).findById(contributorId);
	}

	@DisplayName("기여자 수정 실패")
	@Test
	void failed_updateContributor() {
		// given
		Long contributorId = 999L;
		ContributorRequest request = new ContributorRequest("Lee");

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.empty());

		// then
		ContributorNotFoundException exception = assertThrows(
			ContributorNotFoundException.class,
			() -> contributorService.updateContributor(contributorId, request)
		);

		assertEquals("해당 기여자가 존재하지 않습니다.", exception.getMessage());
		verify(contributorRepository, times(1)).findById(contributorId);
		verify(contributorRepository, never()).save(any());
	}

	@DisplayName("기여자 조회 성공")
	@Test
	void getContributor() {
		// given
		Long contributorId = 1L;
		Contributor contributor = register();

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.of(contributor));
		ContributorResponse response = contributorService.getContributor(contributorId);

		// then
		assertNotNull(response);
		assertEquals(contributorId, response.id());
		assertEquals("Kim", response.name());

		verify(contributorRepository, times(1)).findById(contributorId);

	}

	@DisplayName("모든 기여자 조회 성공")
	@Test
	void getAllContributor() {
		// given
		List<Contributor> contributors = List.of(new Contributor(1L, "contributor1"), new Contributor(2L, "contributor2"));

		Pageable pageable = PageRequest.of(0, 10);
		Page<Contributor> contributorPage = new PageImpl<>(contributors, pageable, contributors.size());

		when(contributorRepository.findAll(pageable)).thenReturn(contributorPage);

		// when
		Page<ContributorResponse> responses = contributorService.getAllContributors(pageable);

		// then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(2);
		assertThat(responses.getContent().get(0).name()).isEqualTo("contributor1");

		// 메서드 호출 여부 검증
		verify(contributorRepository).findAll(pageable);
	}
	
	@DisplayName("기여자 조회 실패")
	@Test
	void failed_getContributor() {
		// given
		Long contributorId = 999L;

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.empty());

		// then

		ContributorNotFoundException exception = assertThrows(
			ContributorNotFoundException.class,
			() -> contributorService.getContributor(contributorId)
		);

		assertEquals("해당 기여자가 존재하지 않습니다.", exception.getMessage());

		verify(contributorRepository, times(1)).findById(contributorId);
	}

	@DisplayName("기여자 삭제 성공")
	@Test
	void deleteContributor() {
		// given
		Long contributorId = 1L;
		Contributor existingContributor = register();

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.of(existingContributor));

		contributorService.deleteContributor(contributorId);

		// then
		verify(contributorRepository, times(1)).findById(contributorId);
		verify(contributorRepository, times(1)).delete(existingContributor);
	}

	@DisplayName("기여자 삭제 실패")
	@Test
	void failed_deleteContributor() {
		// given
		Long contributorId = 999L;

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.empty());

		// then
		ContributorNotFoundException exception = assertThrows(
			ContributorNotFoundException.class,
			() -> contributorService.deleteContributor(contributorId)
		);

		assertEquals("해당 기여자가 존재하지 않습니다.", exception.getMessage());
		verify(contributorRepository, times(1)).findById(contributorId);
		verify(contributorRepository, never()).delete(any());
	}

	private Contributor register() {
		return Contributor.builder().id(1L).name("Kim").build();
	}
}