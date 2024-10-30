package shop.nuribooks.books.book.tag.service;

import java.util.List;

import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;

public interface TagService {
	TagResponse registerTag(TagRequest request);

	List<TagResponse> getAllTags();

	TagResponse getTag(Long id);

	TagResponse updateTag(Long id, TagRequest request);

}
