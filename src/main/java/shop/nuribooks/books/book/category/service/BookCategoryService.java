package shop.nuribooks.books.book.category.service;

public interface BookCategoryService {
	void registerBookCategory(Long bookId, Long categoryId);

	void deleteBookCategory(Long bookId, Long categoryId);

}
