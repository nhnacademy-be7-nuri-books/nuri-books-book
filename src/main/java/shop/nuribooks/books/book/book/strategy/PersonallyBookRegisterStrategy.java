package shop.nuribooks.books.book.book.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.service.CategoryRegisterService;

@RequiredArgsConstructor
@Component
public class PersonallyBookRegisterStrategy implements BookRegisterStrategy {

	private final CategoryRegisterService categoryRegisterService;

	@Override
	public void registerCategory(BaseBookRegisterRequest request, Book book) {
		if (request instanceof PersonallyBookRegisterRequest personallyReq) {
			categoryRegisterService.registerPersonallyCategories(personallyReq.getCategoryIds(), book);
		}
	}
}
