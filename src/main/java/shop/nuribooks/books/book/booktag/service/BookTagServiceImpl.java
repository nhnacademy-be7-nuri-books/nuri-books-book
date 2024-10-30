package shop.nuribooks.books.book.booktag.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.dto.BookTagResponse;
import shop.nuribooks.books.book.booktag.entity.BookTag;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

@RequiredArgsConstructor
@Transactional
@Service
public class BookTagServiceImpl implements BookTagService {
	private final BookTagRepository bookTagRepository;
	private final BookRepository bookRepository;
	private final TagRepository tagRepository;

	/**
	 * registerTagToBook : 도서에 태그 등록
	 *
	 * @param request  등록할 도서와 태그 정보를 담은 객체
	 * @return 해당하는 도서와 등록된 태그들이 포함된 BookTagResponse
	 */
	@Override
	public BookTagResponse registerTagToBook(BookTagRequest request) {
		Book book = bookRepository.findById(request.bookId())
			.orElseThrow(() -> new BookNotFoundException(request.bookId()));

		List<Tag> tags = new ArrayList<>();
		for (Long tagId : request.tagId()) {
			Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException("해당 태그가 존재하지 않습니다."));
			tags.add(tag);
		}

		for (Tag tag : tags) {
			BookTag bookTag = BookTag.builder()
				.book(book)
				.tag(tag)
				.build();
			bookTagRepository.save(bookTag);
		}

		List<Long> tagIds = new ArrayList<>();
		for (Tag tag : tags) {
			tagIds.add(tag.getId());
		}

		return BookTagResponse.of(book, request.bookId(), tagIds);
	}

}
