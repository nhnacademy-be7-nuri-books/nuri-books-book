package shop.nuribooks.books.book.tag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.tag.TagAlreadyExistsException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

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
			throw new TagAlreadyExistsException("태그가 이미 등록되어 있습니다.");
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
	public List<TagResponse> getAllTags() {
		List<Tag> tags = tagRepository.findAll().stream().toList();
		return tags.stream()
			.map(TagResponse::of)
			.toList();
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
			.orElseThrow(() -> new TagNotFoundException("태그가 존재하지 않습니다."));

		return TagResponse.of(tag);
	}

}
