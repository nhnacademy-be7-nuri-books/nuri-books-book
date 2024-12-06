package shop.nuribooks.books.book.book.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.service.CategoryRegisterService;
import shop.nuribooks.books.book.category.entity.BookCategory;
import shop.nuribooks.books.book.category.entity.Category;
import shop.nuribooks.books.book.category.repository.BookCategoryRepository;
import shop.nuribooks.books.book.category.repository.CategoryRepository;
import shop.nuribooks.books.exception.category.CategoryNotFoundException;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryRegisterServiceImpl implements CategoryRegisterService {

	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	@Override
	public void registerAladinCategories(String categoryName, Book book) {
		String[] categoryNames = categoryName.split(">");
		Category currentParentCategory = null;

		for (int i = 0; i < categoryNames.length; i++) {
			final String name = categoryNames[i].trim();
			final Category parentCategory = currentParentCategory;

			Optional<Category> categoryOpt = categoryRepository.findByNameAndParentCategory(name, parentCategory);

			currentParentCategory = categoryOpt.orElseGet(() -> {
				Category category = Category.builder()
					.name(name)
					.parentCategory(parentCategory)
					.build();
				return categoryRepository.save(category);
			});

			if (i == categoryNames.length - 1) {
				BookCategory bookCategory = BookCategory.builder()
					.book(book)
					.category(currentParentCategory)
					.build();
				bookCategoryRepository.save(bookCategory);
			}
		}
	}

	@Override
	public void registerPersonallyCategories(List<Long> categoryIds, Book book) {
		for (Long categoryId : categoryIds) {
			Category category = categoryRepository.findById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

			BookCategory bookCategory = BookCategory.builder()
				.book(book)
				.category(category)
				.build();

			bookCategoryRepository.save(bookCategory);
		}
	}
}
