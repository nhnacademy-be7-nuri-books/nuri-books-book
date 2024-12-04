package shop.nuribooks.books.book.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookListResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.TopBookResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.entity.BookStateEnum;
import shop.nuribooks.books.book.book.mapper.BookMapper;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.BookService;
import shop.nuribooks.books.book.book.utility.BookUtils;
import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;
import shop.nuribooks.books.book.bookcontributor.entity.BookContributor;
import shop.nuribooks.books.book.bookcontributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.booktag.service.BookTagService;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.category.service.BookCategoryService;
import shop.nuribooks.books.book.contributor.entity.Contributor;
import shop.nuribooks.books.book.contributor.entity.ContributorRole;
import shop.nuribooks.books.book.contributor.entity.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;
import shop.nuribooks.books.exception.contributor.InvalidContributorRoleException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;
	private final BookMapper bookMapper;
	private final BookContributorRepository bookContributorRepository;
	private final CategoryRepository categoryRepository;
	private final ContributorRepository contributorRepository;
	private final ContributorRoleRepository contributorRoleRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookTagService bookTagService;
	private final BookCategoryService bookCategoryService;

	@Transactional
	@Override
	public void registerBook(BaseBookRegisterRequest reqDto) {
		log.info("Attempting to save book with ISBN: {}", reqDto.getIsbn());

		if (bookRepository.existsByIsbn(reqDto.getIsbn())) {
			log.warn("Book with ISBN {} already exists.", reqDto.getIsbn());
			throw new ResourceAlreadyExistIsbnException(reqDto.getIsbn());
		}

		Publisher publisher = publisherRepository.findByName(reqDto.getPublisherName())
			.orElseGet(() -> publisherRepository.save(Publisher.builder()
				.name(reqDto.getPublisherName())
				.build()));

		BookStateEnum bookStateEnum = BookStateEnum.fromStringKor(reqDto.getState());

		Book book = bookRepository.save(reqDto.toEntity(publisher, bookStateEnum));
		log.info("Book entity saved: {}", book);

		List<ParsedContributor> parsedContributors = parseContributors(reqDto.getAuthor());
		saveContributors(parsedContributors, book);

		if (reqDto instanceof AladinBookRegisterRequest aladinReq) {
			registerAladinCategories(aladinReq.getCategoryName(), book);
		} else if (reqDto instanceof PersonallyBookRegisterRequest personallyReq) {
			registerPersonallyCategories(personallyReq.getCategoryIds(), book);
		}

		if (reqDto.getTagIds() != null && !reqDto.getTagIds().isEmpty()) {
			bookTagService.registerTagToBook(book.getId(), reqDto.getTagIds());
		}

		log.info("Book with ISBN {} saved successfully", reqDto.getIsbn());
	}

	//도서 상세 조회 시 조회수 증가 추가
	@Transactional
	@Override
	public BookResponse getBookById(Long bookId, boolean updateRecentView) {
		Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		if (updateRecentView)
			book.incrementViewCount();

		return bookMapper.toBookResponse(book);
	}

	@Override
	public Page<BookContributorsResponse> getBooks(Pageable pageable) {
		if (pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException();
		}

		List<BookListResponse> books = bookRepository.findAllWithPublisher(pageable);

		List<BookContributorsResponse> bookListResponses = books.stream()
			.map(book -> {
				// BookListResponse bookDetails = BookListResponse.of(book);
				// log.info("Fetching contributors for bookId: {}", book.getId());
				List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
					book.id());
				Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributors);
				log.info("Contributors fetched: {}", contributors);

				return new BookContributorsResponse(book, contributorsByRole);
			})
			.toList();

		long count = this.bookRepository.countBook();

		return new PageImpl<>(
			bookListResponses,
			pageable,
			count
		);
	}

	@Transactional
	@Override
	public void updateBook(Long bookId, BookUpdateRequest bookUpdateReq) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		book.updateBookDetails(bookUpdateReq);

		bookTagService.deleteBookTagIds(bookId);
		bookTagService.registerTagToBook(book.getId(), bookUpdateReq.tagIds());

		bookCategoryService.deleteBookCategories(bookId);
		registerPersonallyCategories(bookUpdateReq.categoryIds(), book);
	}

	//관리자페이지에서 관리자의 도서 삭제 기능
	@Transactional
	@Override
	public void deleteBook(Long bookId) {
		log.info("deleteBook attempt - bookId: {}", bookId);

		if (bookId == null) {
			log.error("Book Delete fail - bookId is Null");
			throw new BookIdNotFoundException();
		}
		Book book = bookRepository.findBookByIdAndDeletedAtIsNull(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		book.delete();

		log.info("Delete Complete - bookId: {}", book.getId());
	}

	@Override
	public List<TopBookResponse> getTopBookLikes() {
		List<TopBookResponse> likes = bookRepository.findTopBooksByLikes();
		return likes != null ? likes : List.of();
	}

	@Override
	public List<TopBookResponse> getTopBookScores() {
		List<TopBookResponse> scores = bookRepository.findTopBooksByScore();
		return scores != null ? scores : List.of();
	}

	//Contributor 저장 메서드
	private void saveContributors(List<ParsedContributor> parsedContributors, Book book) {
		for (ParsedContributor parsedContributor : parsedContributors) {
			Contributor contributor = contributorRepository.findByName(parsedContributor.getName())
				.orElseGet(() -> contributorRepository.save(
					Contributor.builder()
						.name(parsedContributor.getName())
						.build()
				));

			ContributorRoleEnum contributorRoleEnum = ContributorRoleEnum.fromStringKor(parsedContributor.getRole());
			ContributorRole contributorRole = contributorRoleRepository.findByName(contributorRoleEnum)
				.orElseGet(() -> contributorRoleRepository.save(
					ContributorRole.builder()
						.name(contributorRoleEnum)
						.build()
				));

			BookContributor bookContributor = BookContributor.builder()
				.book(book)
				.contributor(contributor)
				.contributorRole(contributorRole)
				.build();

			bookContributorRepository.save(bookContributor);
		}
	}

	//category 저장 메서드
	private void registerAladinCategories(String categoryName, Book book) {
		String[] categoryNames = categoryName.split(">");
		Category currentParentCategory = null;

		for (int i = 0; i < categoryNames.length; i++) {
			final String name = categoryNames[i].trim();
			final Category parentCategory = currentParentCategory;

			Optional<Category> categoryOpt = categoryRepository.findByNameAndParentCategory(name, parentCategory);

			currentParentCategory = categoryOpt.orElseGet(() -> {
				Category category = Category.builder()
					.name(name)
					.parentCategory(parentCategory)
					.build();
				return categoryRepository.save(category);
			});

			if (i == categoryNames.length - 1) {
				BookCategory bookCategory = BookCategory.builder()
					.book(book)
					.category(currentParentCategory)
					.build();
				bookCategoryRepository.save(bookCategory);
			}
		}
	}

	//직접등록 시 북카테고리 저장
	private void registerPersonallyCategories(List<Long> categoryIds, Book book) {
		for (Long categoryId : categoryIds) {
			Category category = categoryRepository.findById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

			BookCategory bookCategory = BookCategory.builder()
				.book(book)
				.category(category)
				.build();

			bookCategoryRepository.save(bookCategory);
		}
	}

	/**
	 * 알라딘 api 조회를 통해 author응답을 작가이름과 작가역할로 분리하기위한 메서드
	 * @param author - ex) 모구랭 (지은이), 이르 (원작)  또는 정승례, 최보름, 양지은, 윤희 (지은이)
	 * @return 기여자, 기여자역할 리스트
	 */
	private List<ParsedContributor> parseContributors(String author) {
		List<ParsedContributor> parsedContributors = new ArrayList<>();
		int start = 0;
		int startParenthesisIndex;

		while ((startParenthesisIndex = author.indexOf('(', start)) != -1) {
			int closeParenthesisIndex = author.indexOf(')', startParenthesisIndex);
			if (closeParenthesisIndex == -1) {
				throw new InvalidContributorRoleException("역할 이름이 괄호로 닫히지 않아 작가-역할 저장에 실패했습니다.");
			}

			String names = author.substring(start, startParenthesisIndex).trim();
			String role = author.substring(startParenthesisIndex + 1, closeParenthesisIndex).trim();

			for (String name : names.split(",")) {
				String trimmedName = name.trim();
				if (!trimmedName.isEmpty()) {
					parsedContributors.add(new ParsedContributor(trimmedName, role));
				}
			}
			start = closeParenthesisIndex + 1;
		}

		String remainingNames = author.substring(start).trim();
		if (!remainingNames.isBlank()) {
			throw new InvalidContributorRoleException("입력 마지막에 괄호로 역할이 지정되지 않아 작가-역할 저장에 실패합니다.");
		}

		return parsedContributors;
	}

	@Override
	public List<BookResponse> getAllBooks() {
		List<Book> books = bookRepository.findAllAndDeletedAtIsNull();

		return books.stream()
			.map(bookMapper::toBookResponse)
			.toList();
	}

	/**
	 * 도서 저장과 밀접하게 관련되었다 생각하여 서비스 내부에 해당 클래스를 선언했습니다.
	 * 알라딘 api에서는 기여자에 대한 내용이 <author>모구랭 (지은이), 이르 (원작)</author> 이런식으로 응답이 오기 때문에
	 * 파싱해서 저장하려는 용도입니다.
	 */
	public static class ParsedContributor {
		private final String name;
		private final String role;

		public ParsedContributor(String name, String role) {
			this.name = name;
			this.role = role;
		}

		public String getName() {
			return name;
		}

		public String getRole() {
			return role;
		}
	}
}

