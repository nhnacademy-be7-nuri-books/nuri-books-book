package shop.nuribooks.books.book.tag.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.tag.TagAlreadyExistsException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

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

	@DisplayName("모든 태그 조회")
	@Test
	void getAllTags() {
		//given
		Tag tag1 = Tag.builder().name("tag1").build();
		Tag tag2 = Tag.builder().name("tag2").build();
		List<Tag> tags = List.of(tag1, tag2);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Tag> publisherPage = new PageImpl<>(tags, pageable, tags.size());


		//when
		when(tagRepository.findAll(pageable)).thenReturn(publisherPage);

		Page<TagResponse> response = tagService.getAllTags(pageable);

		//then
		assertThat(response).isNotNull();
		assertThat(response).hasSize(2);
		assertThat(response.getContent().get(0).name()).isEqualTo("tag1");
		assertThat(response.getContent().get(1).name()).isEqualTo("tag2");

		verify(tagRepository).findAll(pageable);

	}

	@DisplayName("특정 태그 조회")
	@Test
	void getTag() {
		//given
		Long id = 1L;
		Tag tag = registerTag();
		tag.setId(id);

		//when
		when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

		TagResponse response = tagService.getTag(id);

		//then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.name()).isEqualTo("tag1");

		verify(tagRepository).findById(id);

	}

	@DisplayName("특정 태그 조회 - 실패")
	@Test
	void failed_getTag() {
		//given
		Long id = 999L;

		//when
		when(tagRepository.findById(id)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> tagService.getTag(id))
			.isInstanceOf(TagNotFoundException.class)
			.hasMessageContaining("태그가 존재하지 않습니다.");

		verify(tagRepository).findById(id);

	}

	@DisplayName("특정 태그 수정")
	@Test
	void updateTag() {
		//given
		Long id = 1L;
		Tag existingTag = registerTag();
		TagRequest request = TagRequest.builder().name("update").build();
		Tag updatedTag = Tag.builder().id(id).name("update").build();

		// when
		when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
		when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

		TagResponse response = tagService.updateTag(id, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("update");

		verify(tagRepository).findById(id);

	}

	@DisplayName("특정 태그 수정 - 실패")
	@Test
	void failed_updateTag() {
		// given
		Long id = 999L;
		TagRequest request = TagRequest.builder().name("nonExistingTag").build();

		// when
		when(tagRepository.findById(id)).thenReturn(Optional.empty());

		// then
		assertThatThrownBy(() -> tagService.updateTag(id, request))
			.isInstanceOf(TagNotFoundException.class)
			.hasMessageContaining("태그가 존재하지 않습니다.");

		verify(tagRepository).findById(id);
		verify(tagRepository, never()).save(any(Tag.class));
	}

	@DisplayName("특정 태그 삭제")
	@Test
	void deleteTag() {
		Tag tag = registerTag();

		when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));

		tagService.deleteTag(tag.getId());

		verify(tagRepository).findById(tag.getId());
		verify(tagRepository).delete(tag);
	}

	@DisplayName("특정 태그 삭제 - 실패")
	@Test
	void failed_deleteTag() {
		Long id = 999L;

		when(tagRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tagService.deleteTag(id)
		).isInstanceOf(TagNotFoundException.class)
			.hasMessage("태그가 존재하지 않습니다.");

		verify(tagRepository).findById(id);

	}
	private Tag registerTag() {
		return Tag.builder().name("tag1").build();
	}
}