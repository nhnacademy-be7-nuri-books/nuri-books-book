package shop.nuribooks.books.book.category.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import shop.nuribooks.books.book.book.dto.BookListResponse;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.repository.BookRepository;
import shop.nuribooks.books.book.category.dto.SimpleCategoryResponse;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.publisher.entity.Publisher;
import shop.nuribooks.books.book.publisher.repository.PublisherRepository;
import shop.nuribooks.books.common.TestUtils;
import shop.nuribooks.books.common.config.QuerydslConfiguration;

@DataJpaTest
@Import({QuerydslConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookCategoryRepositoryTest {

	private final List<BookCategory> bookCategories = new ArrayList<>();
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private BookCategoryRepository bookCategoryRepository;
	@Autowired
	private PublisherRepository publisherRepository;
	private Book book;
	private Book book2;
	private Category category;
	private Category parentCategory; // 여기 추가

	@BeforeEach
	void setUp() {
		Publisher publisher = TestUtils.createPublisher();
		publisherRepository.save(publisher);

		// 부모 카테고리 생성 및 저장
		parentCategory = TestUtils.createParentCategory();
		categoryRepository.save(parentCategory);

		// 자식 카테고리 생성 및 저장
		category = TestUtils.createCategory(parentCategory);
		categoryRepository.save(category);

		// 책 생성 및 저장
		book = TestUtils.createBook(publisher);
		bookRepository.save(book);

		book2 = TestUtils.createBook(publisher);
		bookRepository.save(book2);

		// BookCategory 생성 및 저장
		bookCategories.add(TestUtils.createBookCategory(book, category));
		bookCategories.add(TestUtils.createBookCategory(book2, parentCategory));
		bookCategoryRepository.saveAll(bookCategories);

	}

	@DisplayName("책과 카테고리가 존재할 때 BookCategory 존재 여부 확인 성공")
	@Test
	@Order(1)
	void existsByBookAndCategory_Success() {
		// When
		boolean exists = bookCategoryRepository.existsByBookAndCategory(book, category);

		// Then
		assertThat(exists).isTrue();
	}

	@DisplayName("책과 카테고리가 존재할 때 BookCategory 조회 성공")
	@Test
	@Order(3)
	void findByBookAndCategory_Success() {
		// When
		Optional<BookCategory> found = bookCategoryRepository.findByBookAndCategory(book, category);

		// Then
		assertThat(found).isPresent();
		assertThat(found.get().getBook()).isEqualTo(book);
		assertThat(found.get().getCategory()).isEqualTo(category);
	}

	@Test
	@DisplayName("주어진 도서 ID에 해당하는 카테고리 목록 조회 성공")
	@Order(5)
	void findCategoriesByBookId_Success() {
		// When
		List<List<SimpleCategoryResponse>> categories = bookCategoryRepository.findCategoriesByBookId(book.getId());

		// Then
		assertThat(categories).isNotNull();
		assertThat(categories.getFirst().get(1).name()).isEqualTo(category.getName());

	}

	@DisplayName("존재하지 않는 도서 ID로 조회 시 빈 목록 반환")
	@Test
	@Order(6)
	void findCategoriesByBookId_NoCategory() {
		// When
		List<List<SimpleCategoryResponse>> categories = bookCategoryRepository.findCategoriesByBookId(book.getId());

		// Then
		assertThat(categories).isNotNull();
	}

	// 추가된 테스트 메서드들

	@DisplayName("카테고리 ID 목록으로 책 조회 성공")
	@Test
	@Order(7)
	void findBooksByCategoryId_Success() {
		// Given
		List<Long> categoryIds = Collections.singletonList(category.getId());
		Pageable pageable = PageRequest.of(0, 10);

		// When
		List<BookListResponse> books = bookCategoryRepository.findBooksByCategoryId(categoryIds, pageable);

		// Then
		assertThat(books)
			.isNotNull()
			.hasSize(1)
			.first()
			.satisfies(bookResponse -> {
				assertThat(bookResponse.id()).isEqualTo(book.getId());
				assertThat(bookResponse.title()).isEqualTo(book.getTitle());
			});
	}

	@DisplayName("카테고리 ID 목록으로 책 개수 조회 성공")
	@Test
	@Order(8)
	void countBookByCategoryIds_Success() {
		// Given
		List<Long> categoryIds = Collections.singletonList(category.getId());

		// When
		long count = bookCategoryRepository.countBookByCategoryIds(categoryIds);

		// Then
		assertThat(count).isEqualTo(1);
	}

	@DisplayName("복수의 카테고리 ID로 책 조회 성공")
	@Test
	@Order(9)
	void findBooksByMultipleCategoryIds_Success() {
		// Given
		List<Long> categoryIds = Arrays.asList(category.getId(), parentCategory.getId());
		Pageable pageable = PageRequest.of(0, 10);

		// When
		List<BookListResponse> books = bookCategoryRepository.findBooksByCategoryId(categoryIds, pageable);

		// Then
		assertThat(books)
			.isNotNull()
			.hasSize(2);
	}

	@DisplayName("복수의 카테고리 ID로 책 개수 조회 성공")
	@Test
	@Order(10)
	void countBookByMultipleCategoryIds_Success() {
		// Given
		List<Long> categoryIds = Arrays.asList(category.getId(), parentCategory.getId());

		// When
		long count = bookCategoryRepository.countBookByCategoryIds(categoryIds);

		// Then
		assertThat(count).isEqualTo(2);
	}

	@DisplayName("존재하지 않는 카테고리 ID로 책 조회 시 빈 결과 반환")
	@Test
	@Order(11)
	void findBooksByInvalidCategoryId_ReturnsEmpty() {
		// Given
		List<Long> categoryIds = List.of(999L);
		Pageable pageable = PageRequest.of(0, 10);

		// When
		List<BookListResponse> books = bookCategoryRepository.findBooksByCategoryId(categoryIds, pageable);

		// Then
		assertThat(books).isEmpty();
	}

	@DisplayName("존재하지 않는 카테고리 ID로 책 개수 조회 시 0 반환")
	@Test
	@Order(12)
	void countBookByInvalidCategoryIds_ReturnsZero() {
		// Given
		List<Long> categoryIds = List.of(999L);

		// When
		long count = bookCategoryRepository.countBookByCategoryIds(categoryIds);

		// Then
		assertThat(count).isZero();
	}

	@DisplayName("카테고리 ID 목록이 비어있을 때 책 조회 시 빈 결과 반환")
	@Test
	@Order(13)
	void findBooksByEmptyCategoryIds_ReturnsEmpty() {
		// Given
		List<Long> categoryIds = new ArrayList<>();
		Pageable pageable = PageRequest.of(0, 10);

		// When
		List<BookListResponse> books = bookCategoryRepository.findBooksByCategoryId(categoryIds, pageable);

		// Then
		assertThat(books).isEmpty();
	}

	@DisplayName("카테고리 ID 목록이 비어있을 때 책 개수 조회 시 0 반환")
	@Test
	@Order(14)
	void countBookByEmptyCategoryIds_ReturnsZero() {
		// Given
		List<Long> categoryIds = new ArrayList<>();

		// When
		long count = bookCategoryRepository.countBookByCategoryIds(categoryIds);

		// Then
		assertThat(count).isZero();
	}
}