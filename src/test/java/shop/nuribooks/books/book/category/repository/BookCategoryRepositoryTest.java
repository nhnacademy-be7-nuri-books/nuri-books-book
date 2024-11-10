package shop.nuribooks.books.book.category.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.ArrayList;
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

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BookCategoryRepository bookCategoryRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	private Book book;
	private Category category;
	private List<BookCategory> bookCategories = new ArrayList<>();

	@BeforeEach
	void setUp() {
		Publisher publisher = TestUtils.createPublisher();
		publisherRepository.save(publisher);

		category = TestUtils.createCategory();
		Category parentCategory = category.getParentCategory();
		categoryRepository.save(parentCategory);
		categoryRepository.save(category);

		book = TestUtils.createBook(publisher);
		bookRepository.save(book);
		Book book2 = TestUtils.createBook(publisher);
		bookRepository.save(book2);

		bookCategories.add(TestUtils.createBookCategory(book, category));
		bookCategories.add(TestUtils.createBookCategory(book2, parentCategory));
		bookCategoryRepository.save(bookCategories.get(0));
		bookCategoryRepository.save(bookCategories.get(1));

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
		assertThat(categories.get(0).get(1).name()).isEqualTo(category.getName());

	}

	@DisplayName("존재하지 않는 도서 ID로 조회 시 빈 목록 반환")
	@Test
	@Order(6)
	void findCategoriesByBookId_NoCategory() {
		// Given
		Long nonExistentBookId = 999L;

		// When
		List<List<SimpleCategoryResponse>> categories = bookCategoryRepository.findCategoriesByBookId(book.getId());

		// Then
		assertThat(categories).isNotNull();
	}
}