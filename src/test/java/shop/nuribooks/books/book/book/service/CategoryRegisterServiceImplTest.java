package shop.nuribooks.books.book.book.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.service.impl.CategoryRegisterServiceImpl;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryRegisterServiceImplTest {

	@InjectMocks
	private CategoryRegisterServiceImpl categoryRegisterService;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BookCategoryRepository bookCategoryRepository;

	@Captor
	private ArgumentCaptor<Category> categoryCaptor;

	@Captor
	private ArgumentCaptor<BookCategory> bookCategoryCaptor;

	private String categoryName;
	private Book book;
	private Category domesticBook;
	private Category domesticNovel;
	private Category categoryBoy;
	private Category categoryGuy;
	private List<Long> categoryIds;

	@BeforeEach
	void setUp() {
		categoryName = "국내도서 > 국내소설 > 카테고리보이";
		book = Book.builder().build();

		domesticBook = Category.builder()
			.name("국내도서")
			.parentCategory(null)
			.build();

		ReflectionTestUtils.setField(domesticBook, "id", 1L);

		domesticNovel = Category.builder()
			.name("국내소설")
			.parentCategory(domesticBook)
			.build();

		ReflectionTestUtils.setField(domesticNovel, "id", 2L);

		categoryBoy = Category.builder()
			.name("카테고리보이")
			.parentCategory(domesticNovel)
			.build();

		ReflectionTestUtils.setField(categoryBoy, "id", 3L);

		categoryGuy = Category.builder()
			.name("카테고리가이")
			.parentCategory(domesticNovel)
			.build();

		ReflectionTestUtils.setField(categoryGuy, "id", 4L);

		categoryIds = Arrays.asList(3L, 4L);
	}

	@Test
	@DisplayName("알라딘 api 이용 도서 등록 시 카테고리 저장")
	void registerAladinCategories() {
		when(categoryRepository.findByNameAndParentCategory("국내도서", null))
			.thenReturn(Optional.of(domesticBook));
		when(categoryRepository.findByNameAndParentCategory("국내소설", domesticBook))
			.thenReturn(Optional.of(domesticNovel));
		when(categoryRepository.findByNameAndParentCategory("카테고리보이", domesticNovel))
			.thenReturn(Optional.of(categoryBoy));

		categoryRegisterService.registerAladinCategories(categoryName, book);

		verify(categoryRepository).findByNameAndParentCategory("국내도서", null);
		verify(categoryRepository).findByNameAndParentCategory("국내소설", domesticBook);
		verify(categoryRepository).findByNameAndParentCategory("카테고리보이", domesticNovel);
		verify(categoryRepository, never()).save(any(Category.class));

		verify(bookCategoryRepository).save(bookCategoryCaptor.capture());
		BookCategory savedBookCategory = bookCategoryCaptor.getValue();
		assertEquals(book, savedBookCategory.getBook());

		assertEquals(categoryBoy.getId(), savedBookCategory.getCategory().getId());
		assertEquals(categoryBoy.getName(), savedBookCategory.getCategory().getName());
		assertEquals(categoryBoy.getParentCategory(), savedBookCategory.getCategory().getParentCategory());
	}

	@Test
	@DisplayName("알라딘 api 이용 도서 등록 시 새 카테고리 저장")
	void registerAladinNewCategories() {
		final long[] currentId = {1L};

		when(categoryRepository.findByNameAndParentCategory(eq("국내도서"), isNull()))
			.thenReturn(Optional.empty());
		when(categoryRepository.findByNameAndParentCategory(eq("국내소설"), any(Category.class)))
			.thenReturn(Optional.empty());
		when(categoryRepository.findByNameAndParentCategory(eq("카테고리보이"), any(Category.class)))
			.thenReturn(Optional.empty());

		when(categoryRepository.save(any(Category.class)))
			.thenAnswer(invocation -> {
				Category category = invocation.getArgument(0);
				ReflectionTestUtils.setField(category, "id", currentId[0]++);
				return category;
			});

		categoryRegisterService.registerAladinCategories(categoryName, book);

		verify(categoryRepository, times(3)).save(categoryCaptor.capture());
		List<Category> savedCategories = categoryCaptor.getAllValues();

		assertEquals("국내도서", savedCategories.getFirst().getName());
		assertNull(savedCategories.getFirst().getParentCategory());

		assertEquals("국내소설", savedCategories.get(1).getName());
		assertEquals(savedCategories.getFirst(), savedCategories.get(1).getParentCategory());

		assertEquals("카테고리보이", savedCategories.get(2).getName());
		assertEquals(savedCategories.get(1), savedCategories.get(2).getParentCategory());

		verify(bookCategoryRepository).save(bookCategoryCaptor.capture());
		BookCategory savedBookCategory = bookCategoryCaptor.getValue();
		assertEquals(book, savedBookCategory.getBook());
		assertEquals(savedCategories.get(2), savedBookCategory.getCategory());
	}

	@Test
	@DisplayName("직접 도서 등록 시 카테고리 저장")
	void registerPersonallyCategories() {
		when(categoryRepository.findById(3L)).thenReturn(Optional.of(categoryBoy));
		when(categoryRepository.findById(4L)).thenReturn(Optional.of(categoryGuy));

		categoryRegisterService.registerPersonallyCategories(categoryIds, book);

		for (Long categoryId : categoryIds) {
			verify(categoryRepository).findById(categoryId);
		}

		verify(bookCategoryRepository, times(2)).save(bookCategoryCaptor.capture());
		List<BookCategory> savedBookCategory = bookCategoryCaptor.getAllValues();
		assertEquals(2, savedBookCategory.size());

		for (int i = 0; i < categoryIds.size(); i++) {
			assertEquals(book, savedBookCategory.get(i).getBook());
			assertEquals(categoryIds.get(i), savedBookCategory.get(i).getCategory().getId());
		}
	}
}
