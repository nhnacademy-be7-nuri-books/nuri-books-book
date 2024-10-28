package shop.nuribooks.books.book.contributor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import shop.nuribooks.books.book.contributor.dto.ContributorRequest;
import shop.nuribooks.books.book.contributor.dto.ContributorResponse;
import shop.nuribooks.books.book.contributor.entitiy.Contributor;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
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
		ContributorRequest request = new ContributorRequest("Kim");
		Contributor savedContributor = new Contributor();
		savedContributor.setId(1L);
		savedContributor.setName(request.getName());

		// when
		when(contributorRepository.save(any(Contributor.class))).thenReturn(savedContributor);

		ContributorResponse response = contributorService.registerContributor(request);

		// then
		assertNotNull(response);
		assertEquals(1L, response.getId());
		assertEquals("Kim", response.getName());

		verify(contributorRepository, times(1)).save(any(Contributor.class));
	}

	@DisplayName("기여자 수정 성공")
	@Test
	void updateContributor() {
		// given
		Long contributorId = 1L;
		ContributorRequest request = new ContributorRequest("Lee");
		Contributor existingContributor = new Contributor();
		existingContributor.setId(contributorId);
		existingContributor.setName("Kim");

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.of(existingContributor));
		when(contributorRepository.save(existingContributor)).thenReturn(existingContributor);

		ContributorResponse response = contributorService.updateContributor(contributorId, request);

		// then
		assertNotNull(response);
		assertEquals(contributorId, response.getId());
		assertEquals("Lee", response.getName());

		verify(contributorRepository, times(1)).findById(contributorId);
		verify(contributorRepository, times(1)).save(existingContributor);
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
		Contributor contributor = new Contributor();
		contributor.setId(contributorId);
		contributor.setName("Kim");

		// when
		when(contributorRepository.findById(contributorId)).thenReturn(Optional.of(contributor));
		ContributorResponse response = contributorService.getContributor(contributorId);

		// then
		assertNotNull(response);
		assertEquals(contributorId, response.getId());
		assertEquals("Kim", response.getName());

		verify(contributorRepository, times(1)).findById(contributorId);

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
		Contributor existingContributor = new Contributor();
		existingContributor.setId(contributorId);
		existingContributor.setName("Kim");

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

	@Test
	void shouldThrowExceptionWhenContributorNotFound() {
		Long nonExistentId = 999L;
		when(contributorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		assertThrows(ContributorNotFoundException.class, () -> {
			contributorService.getContributor(nonExistentId);
		});
	}
}