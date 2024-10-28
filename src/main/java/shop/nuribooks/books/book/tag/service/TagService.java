package shop.nuribooks.books.book.tag.service;

import shop.nuribooks.books.book.tag.dto.TagRequest;
import shop.nuribooks.books.book.tag.dto.TagResponse;

public interface TagService {
	TagResponse registerTag(TagRequest request);
}
