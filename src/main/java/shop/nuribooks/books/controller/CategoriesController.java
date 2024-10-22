package shop.nuribooks.books.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shop.nuribooks.books.dto.category.request.CategoryRegisterReqDto;
import shop.nuribooks.books.dto.category.response.CategoryRegisterResDto;
import shop.nuribooks.books.service.category.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {
	private final CategoryService categoryService;

	public CategoriesController(CategoryService categoryServiceImpl) {
		this.categoryService = categoryServiceImpl;
	}

	@PostMapping
	public ResponseEntity<CategoryRegisterResDto> createCategory(@RequestBody CategoryRegisterReqDto dto) {
		CategoryRegisterResDto categoryRegisterResDto = new CategoryRegisterResDto(
			categoryService.registerCategory(dto));
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(categoryRegisterResDto);
	}
}
