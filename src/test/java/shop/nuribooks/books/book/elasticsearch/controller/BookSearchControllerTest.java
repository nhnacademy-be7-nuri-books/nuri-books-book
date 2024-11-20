package shop.nuribooks.books.book.elasticsearch.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import shop.nuribooks.books.book.elasticsearch.docs.BookDocument;
import shop.nuribooks.books.book.elasticsearch.enums.SearchType;
import shop.nuribooks.books.book.elasticsearch.enums.SortType;
import shop.nuribooks.books.book.elasticsearch.service.BookSearchService;

@WebMvcTest(BookSearchController.class)
public class BookSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookSearchService bookSearchService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testSearchBooks() throws Exception {
		// Given
		String keyword = "Java";
		SearchType searchType = SearchType.ALL;
		SortType sortType = SortType.ACCURACY;
		PageRequest pageable = PageRequest.of(0, 10);

		BookDocument book1 = new BookDocument();
		book1.setId(1L);
		book1.setTitle("Java in Action");
		book1.setDescription("Comprehensive guide to Java");
		book1.setPrice(BigDecimal.valueOf(35.99));

		BookDocument book2 = new BookDocument();
		book2.setId(2L);
		book2.setTitle("Advanced Java");
		book2.setDescription("Deep dive into Java features");
		book2.setPrice(BigDecimal.valueOf(45.99));

		Page<BookDocument> bookPage = new PageImpl<>(List.of(book1, book2), pageable, 2);

		when(bookSearchService.searchBooks(eq(keyword), eq(searchType), eq(sortType), eq(pageable))).thenReturn(
			bookPage);

		// When & Then
		mockMvc.perform(get("/api/books/search")
				.param("keyword", keyword)
				.param("search_type", searchType.name())
				.param("sort_type", sortType.name())
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].title").value("Java in Action"))
			.andExpect(jsonPath("$.content[1].title").value("Advanced Java"))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	public void testSearchBooksWithWrongSearchType() throws Exception {
		// Given
		String keyword = "Java";
		String searchType = "ie";
		SortType sortType = SortType.ACCURACY;
		PageRequest pageable = PageRequest.of(0, 10);

		BookDocument book1 = new BookDocument();
		book1.setId(1L);
		book1.setTitle("Java in Action");
		book1.setDescription("Comprehensive guide to Java");
		book1.setPrice(BigDecimal.valueOf(35.99));

		BookDocument book2 = new BookDocument();
		book2.setId(2L);
		book2.setTitle("Advanced Java");
		book2.setDescription("Deep dive into Java features");
		book2.setPrice(BigDecimal.valueOf(45.99));

		Page<BookDocument> bookPage = new PageImpl<>(List.of(book1, book2), pageable, 2);

		when(bookSearchService.searchBooks(eq(keyword), any(), eq(sortType), eq(pageable))).thenReturn(
			bookPage);

		// When & Then
		mockMvc.perform(get("/api/books/search")
				.param("keyword", keyword)
				.param("search_type", searchType)
				.param("sort_type", sortType.name())
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].title").value("Java in Action"))
			.andExpect(jsonPath("$.content[1].title").value("Advanced Java"))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}

	@Test
	public void testSearchBooksWithWrongSortType() throws Exception {
		// Given
		String keyword = "Java";
		SearchType searchType = SearchType.ALL;
		String sortType = "SortType.ACCURACY";
		PageRequest pageable = PageRequest.of(0, 10);

		BookDocument book1 = new BookDocument();
		book1.setId(1L);
		book1.setTitle("Java in Action");
		book1.setDescription("Comprehensive guide to Java");
		book1.setPrice(BigDecimal.valueOf(35.99));

		BookDocument book2 = new BookDocument();
		book2.setId(2L);
		book2.setTitle("Advanced Java");
		book2.setDescription("Deep dive into Java features");
		book2.setPrice(BigDecimal.valueOf(45.99));

		Page<BookDocument> bookPage = new PageImpl<>(List.of(book1, book2), pageable, 2);

		when(bookSearchService.searchBooks(eq(keyword), eq(searchType), any(), eq(pageable))).thenReturn(
			bookPage);

		// When & Then
		mockMvc.perform(get("/api/books/search")
				.param("keyword", keyword)
				.param("search_type", searchType.name())
				.param("sort_type", sortType)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].title").value("Java in Action"))
			.andExpect(jsonPath("$.content[1].title").value("Advanced Java"))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(1));
	}
}
