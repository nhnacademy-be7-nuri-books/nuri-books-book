package shop.nuribooks.books.book.book.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.AdminBookListResponse;
import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookContributorsResponse;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.dto.BookUpdateRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
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
import shop.nuribooks.books.common.message.PagedResponse;
import shop.nuribooks.books.exception.InvalidPageRequestException;
import shop.nuribooks.books.exception.book.BookIdNotFoundException;
import shop.nuribooks.books.exception.book.InvalidBookStateException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

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

		try {
			Publisher publisher;
			try {
				publisher = publisherRepository.findByName(reqDto.getPublisherName())
					.orElseGet(() -> publisherRepository.save(Publisher.builder()
						.name(reqDto.getPublisherName())
						.build()
					));
			} catch (Exception ex) {
				log.error("Error saving book entity: {}", ex.getMessage(), ex);
				throw ex;
			}

			BookStateEnum bookStateEnum;
			try {
				bookStateEnum = BookStateEnum.fromStringKor(String.valueOf(reqDto.getState()));
			} catch (InvalidBookStateException ex) {
				log.error("Error parsing book state from request: {}", ex.getMessage(), ex);
				throw ex;
			}

			Book book;
			try {
				book = reqDto.toEntity(publisher, bookStateEnum);
				log.info("Saving book entity: {}", book);
				bookRepository.save(book);
			} catch (Exception ex) {
				log.error("Error saving book entity: {}", ex.getMessage(), ex);
				throw ex;
			}

			List<ParsedContributor> parsedContributors = List.of();
			try {
				parsedContributors = parseContributors(reqDto.getAuthor());
				saveContributors(parsedContributors, book);
			} catch (Exception ex) {
				log.error("Error parsing or saving contributors: {}", ex.getMessage(), ex);
			}

			try {
				if (reqDto instanceof AladinBookRegisterRequest aladinReq) {
					registerAladinCategories(aladinReq.getCategoryName(), book);
				} else if (reqDto instanceof PersonallyBookRegisterRequest personallyReq) {
					registerPersonallyCategories(personallyReq.getCategoryIds(), book);
				}
			} catch (Exception ex) {
				log.error("Error registering categories: {}", ex.getMessage(), ex);
				throw ex;
			}

			try {
				if (reqDto.getTagIds() != null && !reqDto.getTagIds().isEmpty()) {
					List<Long> tagIdList = reqDto.getTagIds();
					bookTagService.registerTagToBook(book.getId(), tagIdList);
				}
			} catch (Exception ex) {
				log.error("Error registering tags: {}", ex.getMessage(), ex);
				throw ex;
			}
			log.info("Book with ISBN {} successfully saved.", reqDto.getIsbn());

		} catch (Exception ex) {
			log.error("Error saving book with ISBN {}: {}", reqDto.getIsbn(), ex.getMessage(), ex);
			throw ex;
		}
	}

	//도서 상세 조회 시 조회수 증가 추가
	@Transactional
	@Override
	public BookResponse getBookById(Long bookId) {
		Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
			.orElseThrow(BookIdNotFoundException::new);

		book.incrementViewCount();
		bookRepository.save(book);

		return bookMapper.toBookResponse(book);
	}

	@Override
	public PagedResponse<BookContributorsResponse> getBooks(Pageable pageable) {
		if (pageable.getPageNumber() < 0) {
			throw new InvalidPageRequestException("페이지 번호는 0 이상이어야 합니다.");
		}

		Page<Book> bookPage = bookRepository.findAllWithPublisher(pageable);

		if (pageable.getPageNumber() > bookPage.getTotalPages() - 1) {
			return new PagedResponse<>(
				Collections.emptyList(),
				pageable.getPageNumber(),
				pageable.getPageSize(),
				bookPage.getTotalPages(),
				bookPage.getTotalElements()
			);
		}

		List<BookContributorsResponse> bookListResponses = bookPage.stream()
			.map(book -> {
				AdminBookListResponse bookDetails = AdminBookListResponse.of(book);
				log.info("Fetching contributors for bookId: {}", book.getId());
				List<BookContributorInfoResponse> contributors = bookContributorRepository.findContributorsAndRolesByBookId(
					book.getId());
				Map<String, List<String>> contributorsByRole = BookUtils.groupContributorsByRole(contributors);
				log.info("Contributors fetched: {}", contributors);

				return new BookContributorsResponse(bookDetails, contributorsByRole);
			})
			.toList();

		return new PagedResponse<>(
			bookListResponses,
			bookPage.getNumber(),
			bookPage.getSize(),
			bookPage.getTotalPages(),
			bookPage.getTotalElements()
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
	 * ((?:[^,(]+(?:,\\s)?)+): 쉼표로 구분된 여러 단어를 하나의 이름으로 처리
	 * (?:\\s*\\(([^)]+)\\))?: 괄호로 묶인 역할 부분을 선택적으로 매칭
	 * \\s*: 역할 앞에 공백이 있을 수 있으므로 제거.
	 * @param author - ex) 모구랭 (지은이), 이르 (원작)  또는 정승례, 최보름, 양지은, 윤희 (지은이)
	 * @return 기여자, 기여자역할 리스트
	 */
	private List<ParsedContributor> parseContributors(String author) {
		List<ParsedContributor> contributors = new ArrayList<>();
		Matcher matcher = Pattern.compile("((?:[^,(]+(?:,\\s)?)+)(?:\\s*\\(([^)]+)\\))?").matcher(author);

		while (matcher.find()) {
			String names = matcher.group(1).trim();
			String role = matcher.group(2) != null ? matcher.group(2).trim() : "";

			for (String name : names.split("\\s*,\\s*")) {

				contributors.add(new ParsedContributor(name.trim(), role));
			}
		}
		return contributors;
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
