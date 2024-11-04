package shop.nuribooks.books.book.book.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.nuribooks.books.book.book.dto.AladinBookListItemResponse;
import shop.nuribooks.books.book.book.dto.AladinBookListResponse;
import shop.nuribooks.books.book.book.dto.AladinBookSaveRequest;
import shop.nuribooks.books.book.book.dto.BookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BookResponse;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.book.service.AladinBookService;
import shop.nuribooks.books.book.category.entitiy.Category;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.book.client.AladinFeignClient;
import shop.nuribooks.books.book.publisher.entitiy.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.exception.book.PublisherIdNotFoundException;
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
	public BookResponse saveBook(AladinBookSaveRequest reqDto) {
		if (bookRepository.existsByIsbn(reqDto.isbn())) {
			throw new ResourceAlreadyExistIsbnException(reqDto.isbn());
		}

		Publisher publisher = publisherRepository.findById(reqDto.publisherId())
			.orElseThrow(() -> new PublisherIdNotFoundException(reqDto.publisherId()));

		String[] categoryNames = reqDto.categoryName().split(">");
		Category parentCategory = null;

		for (String categoryName : categoryNames) {

		}
		return null;
	}
}
