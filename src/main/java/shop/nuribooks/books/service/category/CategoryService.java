package shop.nuribooks.books.service.category;

import shop.nuribooks.books.dto.category.request.CategoryRegisterReqDto;
import shop.nuribooks.books.entity.book.Category;

public interface CategoryService {
	// register new category
	Category registerCategory(CategoryRegisterReqDto dto);
}
