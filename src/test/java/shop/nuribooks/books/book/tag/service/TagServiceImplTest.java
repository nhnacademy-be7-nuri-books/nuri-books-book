package shop.nuribooks.books.book.tag.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.tag.TagAlreadyExistsException;

@SpringBootTest
class TagServiceImplTest {

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private TagServiceImpl tagService;

	@DisplayName("태그 등록 성공")
	@Test
	void registerTagName() {
		//given
		Tag tag = registerTag();
		TagRequest request = TagRequest.builder().name("tag1").build();

		//when
		when(tagRepository.existsByName(request.name())).thenReturn(false);
		when(tagRepository.save(any(Tag.class))).thenReturn(tag);
		TagResponse response = tagService.registerTag(request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("tag1");

		verify(tagRepository).existsByName(request.name());
		verify(tagRepository).save(any(Tag.class));
	}



	@DisplayName("태그 등록 실패 - 중복")
	@Test
	void failed_registerTag() {
		//given
		registerTag();
		TagRequest request = TagRequest.builder().name("tag1").build();

		//when
		when(tagRepository.existsByName(request.name())).thenReturn(true);

		//then
		assertThatThrownBy(() -> tagService.registerTag(request)).isInstanceOf(
			TagAlreadyExistsException.class).hasMessageContaining("태그가 이미 등록되어 있습니다.");

		verify(tagRepository).existsByName(request.name());
		verify(tagRepository, never()).save(any(Tag.class));
	}

	private Tag registerTag() {
		return Tag.builder().name("tag1").build();
	}
}