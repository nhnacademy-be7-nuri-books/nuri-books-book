package shop.nuribooks.books.book.book.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.entitiy.Book;
import shop.nuribooks.books.book.book.entitiy.BookStateEnum;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.book.category.entitiy.Category;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.client.AladinFeignClient;
import shop.nuribooks.books.book.contributor.entitiy.BookContributor;
import shop.nuribooks.books.book.contributor.entitiy.Contributor;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRole;
import shop.nuribooks.books.book.contributor.entitiy.ContributorRoleEnum;
import shop.nuribooks.books.book.contributor.repository.BookContributorRepository;
import shop.nuribooks.books.book.contributor.repository.ContributorRepository;
import shop.nuribooks.books.book.contributor.repository.role.ContributorRoleRepository;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.ResourceNotFoundException;
import shop.nuribooks.books.exception.book.ResourceAlreadyExistIsbnException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AladinBookServiceImpl implements AladinBookService {
	private final AladinFeignClient aladinFeignClient;
	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;
	private final CategoryRepository categoryRepository;
	private final ContributorRepository contributorRepository;
	private final ContributorRoleRepository contributorRoleRepository;
	private final BookContributorRepository bookContributorRepository;

	@Value("${aladin.api.key}")
	private String ttbKey;

	//도서 리스트 조회 메서드
	//TODO: 추후 파라미터 추가 예정
	@Override
	public List<AladinBookListItemResponse> getNewBooks() {
		try {
			AladinBookListResponse response = aladinFeignClient.getNewBooks(ttbKey, "ItemNewAll", 10, 1, "Book", "JS", "20131101");
			log.info("Received response: {}", response);
			return response.item();
		} catch (Exception ex) {
			log.error("Error: ", ex);
			throw new RuntimeException("Failed to retrieve new books from Aladin");
		}
	}

	@Override
	public AladinBookListItemResponse getBookByIsbn(String isbn) {
		List<AladinBookListItemResponse> books = getNewBooks();
		return books.stream()
			.filter(book -> book.isbn().equals(isbn))
			.findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다."));
	}

	@Transactional
	@Override
	public void saveBook(AladinBookSaveRequest reqDto) {
		if (bookRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		Publisher publisher = publisherRepository.findByName(reqDto.publisherName())
			.orElseGet(() -> publisherRepository.save(Publisher.builder()
				.name(reqDto.publisherName())
				.build()
			));

		//중복코드 추후 수정 예정
		BookStateEnum bookStateEnum = BookStateEnum.fromString(String.valueOf(reqDto.state()));

		Book book = Book.builder()
			.publisherId(publisher)
			.state(bookStateEnum)
			.title(reqDto.title())
			.thumbnailImageUrl(reqDto.thumbnailImageUrl())
			.detailImageUrl(reqDto.detailImageUrl())
			.publicationDate(reqDto.publicationDate())
			.price(reqDto.price())
			.discountRate(reqDto.discountRate())
			.description(reqDto.description())
			.contents(reqDto.contents())
			.isbn(reqDto.isbn())
			.isPackageable(reqDto.isPackageable())
			.likeCount(0)
			.stock(reqDto.stock())
			.viewCount(0L)
			.build();

		bookRepository.save(book);

		List<ParsedContributor> parsedContributors = parseContributors(reqDto.author());
		saveContributors(parsedContributors, book);
		saveCategories(reqDto.categoryName());
	}

	//Contributor 저장 메서드
	private void saveContributors(List<ParsedContributor> parsedContributors, Book book) {
		for(ParsedContributor parsedContributor : parsedContributors) {
			Contributor contributor = contributorRepository.findByName(parsedContributor.getName())
				.orElseGet(() -> contributorRepository.save(
					Contributor.builder()
						.name(parsedContributor.getName())
						.build()
				));

			ContributorRoleEnum contributorRoleEnum = ContributorRoleEnum.fromString(parsedContributor.getRole());
			ContributorRole contributorRole = contributorRoleRepository.findByName(contributorRoleEnum)
				.orElseGet(() -> contributorRoleRepository.save(
					ContributorRole.builder()
						.name(contributorRoleEnum)
						.build()
				));

			BookContributor bookContributor = new BookContributor();
			bookContributor.setBook(book);
			bookContributor.setContributor(contributor);
			bookContributor.setContributorRole(contributorRole);
		}
	}

	//category 저장 메서드
	private List<Category> saveCategories(String categoryName) {
		String[] categoryNames = categoryName.split(">");
		Category currentParentCategory = null;
		List<Category> categories = new ArrayList<>();

		for (String name : categoryNames) {
			final Category parent = currentParentCategory;
			Optional<Category> categoryOpt = categoryRepository.findByNameAndParentCategory(name.trim(), parent);
			currentParentCategory = categoryOpt.orElseGet(() -> {
				Category newCategory = Category.builder()
					.name(name.trim())
					.parentCategory(parent)
					.build();
				return categoryRepository.save(newCategory);
			});
			categories.add(currentParentCategory);
		}
		return categories;
	}

	/**
	 * 알라딘 api 조회를 통해 author응답을 작가이름과 작가역할로 분리하기위한 메서드
	 * ([^,]+?): 쉼표(,)가 나오기 전까지의 모든 문자열을 하나의 그룹으로 캡처 (Contributor)
	 * \\(([^)]+)\\): 괄호 안에 있는 문자열을 또 다른 그룹으로 캡처 (Role)
	 * @param author - ex) 모구랭 (지은이), 이르 (원작)
	 * @return 기여자, 기여자역할 리스트
	 */
	private List<ParsedContributor> parseContributors(String author) {
		List<ParsedContributor> contributors = new ArrayList<>();
		Matcher matcher = Pattern.compile("([^,]+?) \\(([^)]+)\\)").matcher(author);

		while (matcher.find()) {
			String name = matcher.group(1).trim();
			String role = matcher.group(2).trim();
			contributors.add(new ParsedContributor(name,role));
		}
		return contributors;
	}

	/**
	 * 도서 저장과 밀접하게 관련되었다 생각하여 서비스 내부에 해당 클래스를 선언했습니다.
	 * 알라딘 api에서는 기여자에 대한 내용이 <author>모구랭 (지은이), 이르 (원작)</author> 이런식으로 응답이 오기 때문에
	 * 파싱해서 저장하려는 용도입니다.
	 */
	private static class ParsedContributor {
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
