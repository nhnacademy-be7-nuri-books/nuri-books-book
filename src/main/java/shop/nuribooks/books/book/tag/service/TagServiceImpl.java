package shop.nuribooks.books.book.tag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.entity.TagEditor;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.tag.TagAlreadyExistsException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

@Transactional
@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	/**
	 * getTagEditor : 태그 편집 빌더
	 *
	 * @param request 요청된 태그 정보 담긴 객체
	 * @param tag 기존 태그 정보를 담은 객체
	 * @return 수정된 정보를 포함한 객체
	 */
	private static TagEditor getTagEditor(TagRequest request, Tag tag) {
		TagEditor.TagEditorBuilder builder = tag.toEditor();
		return builder
			.name(request.name())
			.build();
	}

	/**
	 * registerTag : 태그 등록
	 *
	 * @param request 태그 이름 등록
	 *                이미 존재하는 태그일생경우 TagAlreadyExistsException 발생
	 * @return 등록된 태그 이름을 포함한 TagResponse
	 */
	@Override
	public TagResponse registerTag(TagRequest request) {
		if (tagRepository.existsByName(request.name())) {
			throw new TagAlreadyExistsException();
		}
		Tag tag = request.toEntity();
		Tag savedTag = tagRepository.save(tag);

		return TagResponse.of(savedTag);
	}

	/**
	 * getAllTags : 등록 되어있는 모든 태그 조회
	 *
	 * @return 등록된 태그 정보를 포함한 TagResponse List
	 */
	@Override
	public Page<TagResponse> getAllTags(Pageable pageable) {
		Page<Tag> tags = tagRepository.findAll(pageable);
		return tags.map(TagResponse::of);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public List<TagResponse> getAllTags() {
		List<Tag> tags = tagRepository.findAll();
		return tags.stream().map(TagResponse::of).toList();
	}

	/**
	 * getTag : 태그 조회
	 *
	 * @param id id로 태그 정보 조회
	 *           존재하지 않는 태그일 경우 TagNotFoundException 발생
	 * @return id에 해당하는 태그 정보 TagResponse
	 */
	@Override
	public TagResponse getTag(Long id) {
		Tag tag = tagRepository.findById(id)
			.orElseThrow(TagNotFoundException::new);

		return TagResponse.of(tag);
	}

	/**
	 * updateTag : 태그 수정
	 *
	 * @param id id로 태그 정보 조회
	 *           존재하지 않는 태그일 경우 TagNotFoundException 발생
	 * @param request 수정할 태그 정보를 담은 객체
	 * @return id에 해당하는 수정된 태그 정보 TagResponse
	 */
	@Override
	public TagResponse updateTag(Long id, TagRequest request) {
		Tag tag = tagRepository.findById(id)
			.orElseThrow(TagNotFoundException::new);

		if (tagRepository.existsByName(request.name())) {
			throw new TagAlreadyExistsException();
		}

		TagEditor tagEditor = getTagEditor(request, tag);
		tag.edit(tagEditor);
		return TagResponse.of(tag);
	}

	/**
	 * deleteTag : 태그 정보 삭제
	 *
	 * @param id id로 태그 정보 조회
	 *           존재하지 않는 태그일 경우 TagNotFoundException 발생
	 */
	@Override
	public void deleteTag(Long id) {
		Tag tag = tagRepository.findById(id)
			.orElseThrow(TagNotFoundException::new);

		tagRepository.delete(tag);
	}

}
