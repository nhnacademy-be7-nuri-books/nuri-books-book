package shop.nuribooks.books.book.booktag.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.booktag.dto.BookTagGetResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRegisterResponse;
import shop.nuribooks.books.book.booktag.dto.BookTagRequest;
import shop.nuribooks.books.book.booktag.entity.BookTag;
import shop.nuribooks.books.book.booktag.repository.BookTagRepository;
import shop.nuribooks.books.book.tag.entity.Tag;
import shop.nuribooks.books.book.tag.repository.TagRepository;
import shop.nuribooks.books.exception.book.BookNotFoundException;
import shop.nuribooks.books.exception.tag.BookTagAlreadyExistsException;
import shop.nuribooks.books.exception.tag.BookTagNotFountException;
import shop.nuribooks.books.exception.tag.TagNotFoundException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookTagServiceImpl implements BookTagService {
	private final BookTagRepository bookTagRepository;
	private final BookRepository bookRepository;
	private final TagRepository tagRepository;
	private final BookMapper bookMapper;

	/**
	 * registerTagToBook : 도서에 태그 등록
	 *
	 * @param request 등록할 도서와 태그 정보를 담은 객체
	 * @throws BookNotFoundException 도서 id가 없을 때 예외
	 * @throws TagNotFoundException 태그 id가 없을 때 예외
	 * @throws BookTagAlreadyExistsException 도서에 태그가 이미 등록되어 있는 경우 중복 예외
	 *
	 * @return 해당하는 도서와 등록된 태그들이 포함된 BookTagResponse
	 */
	@Transactional
	@Override
	public BookTagRegisterResponse registerTagToBook(BookTagRequest request) {
		Book book = bookRepository.findById(request.bookId())
			.orElseThrow(() -> new BookNotFoundException(request.bookId()));

		List<Long> tagIds = new ArrayList<>();

		for (Long tagId : request.tagId()) {
			Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException());

			if (!bookTagRepository.existsByBookIdAndTagId(request.bookId(), tagId)) {
				BookTag bookTag = BookTag.builder()
					.book(book)
					.tag(tag)
					.build();
				bookTagRepository.save(bookTag);
				tagIds.add(tagId);
			} else {
				throw new BookTagAlreadyExistsException();
			}
		}

		return BookTagRegisterResponse.of(book, request.bookId(), tagIds);
	}

	/**
	 * getBookTag : 도서에 등록된 태그 조회
	 *
	 * @param bookId 조회할 도서 id
	 * @throws BookNotFoundException 도서 id가 없을 때 예외
	 *
	 * @return 해당하는 도서와 등록된 태그들이 포함된 BookTagResponse
	 */
	@Override
	public BookTagGetResponse getBookTag(Long bookId) {

		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		List<String> tagNames = bookTagRepository.findTagNamesByBookId(bookId);

		List<BookTag> bookTags = bookTagRepository.findByBookId(bookId);

		Long bookTagId = bookTags.isEmpty() ? null : bookTags.get(0).getId();

		return BookTagGetResponse.of(bookTagId, bookId, tagNames);
	}

	/**
	 * getBooksByTagId : 해당하는 태그가 달린 모든 도서 조회
	 *
	 * @param tagId 조회할 태그 id
	 * @throws TagNotFoundException 태그 id가 없을 때 예외
	 *
	 * @return 해당하는 도서 정보들이 포함된 리스트 형식의 BookResponse
	 */
	@Override
	public List<BookResponse> getBooksByTagId(Long tagId) {
		tagRepository.findById(tagId)
			.orElseThrow(() -> new TagNotFoundException());

		List<Long> bookIds = bookTagRepository.findBookIdsByTagId(tagId);

		List<Book> books = bookRepository.findAllById(bookIds);

		return books.stream()
			.map(bookMapper::toBookResponse)
			.collect(Collectors.toList());
	}

	/**
	 * deleteBookTag : 해당하는 도서태그 삭제
	 * @param bookTagId 삭제할 도서태그 id
	 * @throws BookTagNotFountException 도서태그 id가 없을 때 예외
	 */
	@Transactional
	@Override
	public void deleteBookTag(Long bookTagId) {
		BookTag bookTag = bookTagRepository.findById(bookTagId)
			.orElseThrow(() -> new BookTagNotFountException());
		bookTagRepository.delete(bookTag);
	}

	@Transactional
	public void deleteBookTagIds(Long bookId) {
		List<BookTag> bookTags = bookTagRepository.findByBookId(bookId);
		bookTagRepository.deleteAll(bookTags);
	}

	@Transactional
	@Override
	public void registerTagToBook(Long bookId, List<Long> tagIds) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException(bookId));

		for (Long tagId : tagIds) {
			Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new TagNotFoundException());

			if (!bookTagRepository.existsByBookIdAndTagId(bookId, tagId)) {
				BookTag bookTag = BookTag.builder()
					.book(book)
					.tag(tag)
					.build();
				bookTagRepository.save(bookTag);
			} else {
				throw new BookTagAlreadyExistsException();
			}
		}
	}
}
