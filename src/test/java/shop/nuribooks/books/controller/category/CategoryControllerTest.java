package shop.nuribooks.books.controller.category;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.nuribooks.books.controller.GlobalExceptionHandler;
import shop.nuribooks.books.dto.category.CategoryRequest;
import shop.nuribooks.books.dto.category.CategoryResponse;
import shop.nuribooks.books.entity.book.Category;
import shop.nuribooks.books.exception.category.CategoryAlreadyExistException;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;
import shop.nuribooks.books.service.category.CategoryService;

/**
 * CategoryController의 기능을 테스트하는 클래스.
 */
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(categoryService))
			.setControllerAdvice(new GlobalExceptionHandler()) // 글로벌 예외 처리기 추가
			.build();
	}

	/**
	 * 유효한 요청을 통해 대분류 카테고리를 등록할 때, HTTP 상태 코드 201(Created)을 반환하는지 테스트합니다.
	 */
	@Test
	void registerMainCategory_whenValidRequest_thenReturnsCreated() throws Exception {
		// given
		CategoryRequest dto = new CategoryRequest("여행");
		Category category = Category.builder().name("여행").build();
		CategoryResponse response = new CategoryResponse(category);
		when(categoryService.registerMainCategory(any(CategoryRequest.class))).thenReturn(category);

		// when & then
		mockMvc.perform(post("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("여행"));
	}

	/**
	 * 유효한 요청을 통해 하위 분류 카테고리를 등록할 때, HTTP 상태 코드 201(Created)을 반환하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenValidRequest_thenReturnsCreated() throws Exception {
		// given
		Long parentCategoryId = 1L;
		CategoryRequest dto = new CategoryRequest("국내 여행");
		Category parentCategory = Category.builder().name("여행").build();
		Category subCategory = Category.builder().name("국내 여행").parentCategory(parentCategory).build();
		CategoryResponse response = new CategoryResponse(subCategory);
		when(categoryService.registerSubCategory(any(CategoryRequest.class), eq(parentCategoryId))).thenReturn(
			subCategory);

		// when & then
		mockMvc.perform(post("/api/categories/{categoryId}", parentCategoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("국내 여행"));
	}

	/**
	 * 대분류 카테고리가 이미 존재할 때, HTTP 상태 코드 409(Conflict)을 반환하는지 테스트합니다.
	 */
	@Test
	void registerMainCategory_whenCategoryAlreadyExists_thenThrowsException() throws Exception {
		// given
		CategoryRequest dto = new CategoryRequest("여행");
		doThrow(new CategoryAlreadyExistException("Top-level category with name '여행' already exists.")).when(
			categoryService).registerMainCategory(any(CategoryRequest.class));

		// when & then
		mockMvc.perform(post("/api/categories")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("Top-level category with name '여행' already exists."));
	}

	/**
	 * 부모 카테고리를 찾을 수 없는 경우, HTTP 상태 코드 404(Not Found)를 반환하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenParentCategoryNotFound_thenThrowsException() throws Exception {
		// given
		Long parentCategoryId = 1L;
		CategoryRequest dto = new CategoryRequest("국내 여행");
		doThrow(new CategoryNotFoundException("Parent category not found with ID: " + parentCategoryId)).when(
			categoryService).registerSubCategory(any(CategoryRequest.class), eq(parentCategoryId));

		// when & then
		mockMvc.perform(post("/api/categories/{categoryId}", parentCategoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Parent category not found with ID: " + parentCategoryId));
	}

	/**
	 * 동일한 이름의 하위 카테고리가 이미 존재하는 경우, HTTP 상태 코드 409(Conflict)을 반환하는지 테스트합니다.
	 */
	@Test
	void registerSubCategory_whenSubCategoryAlreadyExists_thenThrowsException() throws Exception {
		// given
		Long parentCategoryId = 1L;
		CategoryRequest dto = new CategoryRequest("국내 여행");
		doThrow(new CategoryAlreadyExistException(
			"Category with name '국내 여행' already exists under parent category with ID: " + parentCategoryId)).when(
			categoryService).registerSubCategory(any(CategoryRequest.class), eq(parentCategoryId));

		// when & then
		mockMvc.perform(post("/api/categories/{categoryId}", parentCategoryId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value(
				"Category with name '국내 여행' already exists under parent category with ID: " + parentCategoryId));
	}

	/**
	 * 모든 카테고리를 조회할 때, HTTP 상태 코드 200(OK)을 반환하고 올바른 응답을 받는지 테스트합니다.
	 */
	@Test
	void getAllCategories_whenCalled_thenReturnsOk() throws Exception {
		// given
		List<CategoryResponse> categories = List.of(
			new CategoryResponse(Category.builder().name("여행").build()),
			new CategoryResponse(Category.builder().name("국내 여행").build())
		);
		when(categoryService.getAllCategory()).thenReturn(categories);

		// when & then
		mockMvc.perform(get("/api/categories")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].name").value("여행"))
			.andExpect(jsonPath("$[1].name").value("국내 여행"));
	}

	/**
	 * 유효한 ID로 특정 카테고리를 조회할 때, HTTP 상태 코드 200(OK)을 반환하고 올바른 카테고리 데이터를 받는지 테스트합니다.
	 */
	@Test
	void getCategoryById_whenValidId_thenReturnsOk() throws Exception {
		// given
		Long categoryId = 1L;
		Category category = Category.builder().name("여행").build();
		CategoryResponse response = new CategoryResponse(category);
		when(categoryService.getCategoryById(categoryId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("여행"));
	}

	/**
	 * 유효하지 않은 ID로 특정 카테고리를 조회할 때, HTTP 상태 코드 404(Not Found)를 반환하는지 테스트합니다.
	 */
	@Test
	void getCategoryById_whenInvalidId_thenReturnsNotFound() throws Exception {
		// given
		Long categoryId = 999L;
		doThrow(new CategoryNotFoundException("Category not found with ID: " + categoryId)).when(categoryService)
			.getCategoryById(categoryId);

		// when & then
		mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Category not found with ID: " + categoryId));
	}

}
