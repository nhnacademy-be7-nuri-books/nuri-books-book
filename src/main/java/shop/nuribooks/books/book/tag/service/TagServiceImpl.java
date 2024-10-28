package shop.nuribooks.books.book.tag.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.tag.TagAlreadyExistsException;

@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	/**
	 * registerTag : 태그 등록
	 * @author kyongmin
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
}
