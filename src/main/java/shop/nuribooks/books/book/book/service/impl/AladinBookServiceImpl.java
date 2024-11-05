package shop.nuribooks.books.book.book.service.impl;

import java.util.List;
import java.util.Optional;

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
	public BookResponse saveBook(AladinBookSaveRequest reqDto) {
		if (bookRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		Publisher publisher = publisherRepository.findByName(reqDto.publisherName())
			.orElseGet(() -> {
				Publisher newPublisher = Publisher.builder()
					.name(reqDto.publisherName())
					.build();
				return publisherRepository.save(newPublisher);
			});



		BookStateEnum bookStateEnum = BookStateEnum.fromString(String.valueOf(reqDto.state()));
		//BookStateEnum bookStateEnum = reqDto.state() == null ? BookStateEnum.NORMAL_DISTRIBUTION : reqDto.state();

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

		String[] categoryNames = reqDto.categoryName().split(">");
		Category currentParentCategory = null;

		for (String categoryName : categoryNames) {
			final Category parent = currentParentCategory;
			Optional<Category> categoryOpt = categoryRepository.findByNameAndParentCategory(categoryName.trim(), parent);
			Category category = categoryOpt.orElseGet(() -> {
				Category newCategory = Category.builder()
					.name(categoryName.trim())
					.parentCategory(parent)
					.build();
				return categoryRepository.save(newCategory);
			});
			currentParentCategory = category;
		}
		return BookResponse.of(book);
	}
}
