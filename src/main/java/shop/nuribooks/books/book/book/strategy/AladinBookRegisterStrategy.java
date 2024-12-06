package shop.nuribooks.books.book.book.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.book.book.dto.request.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.request.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.entity.Book;
import shop.nuribooks.books.book.book.service.CategoryRegisterService;

@RequiredArgsConstructor
@Component
public class AladinBookRegisterStrategy implements BookRegisterStrategy {

	private final CategoryRegisterService categoryRegisterService;

	@Override
	public void registerCategory(BaseBookRegisterRequest request, Book book) {
		if (request instanceof AladinBookRegisterRequest aladinReq) {
			categoryRegisterService.registerAladinCategories(aladinReq.getCategoryName(), book);
		}
	}
}
