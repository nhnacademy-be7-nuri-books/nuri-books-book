package shop.nuribooks.books.book.tag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;

public interface TagService {
	TagResponse registerTag(TagRequest request);

	Page<TagResponse> getAllTags(Pageable pageable);

	TagResponse getTag(Long id);

	TagResponse updateTag(Long id, TagRequest request);

	void deleteTag(Long id);
}
