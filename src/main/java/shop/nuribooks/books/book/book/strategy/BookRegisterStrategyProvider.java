package shop.nuribooks.books.book.book.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import shop.nuribooks.books.book.book.dto.AladinBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.BaseBookRegisterRequest;
import shop.nuribooks.books.book.book.dto.PersonallyBookRegisterRequest;

@Component
public class BookRegisterStrategyProvider {

	private final Map<Class<? extends BaseBookRegisterRequest>, BookRegisterStrategy> strategyMap = new HashMap<>();

	@Autowired
	public BookRegisterStrategyProvider(
		AladinBookRegisterStrategy aladinStrategy,
		PersonallyBookRegisterStrategy personallyStrategy
	) {
		strategyMap.put(AladinBookRegisterRequest.class, aladinStrategy);
		strategyMap.put(PersonallyBookRegisterRequest.class, personallyStrategy);
	}

	public BookRegisterStrategy getStrategy(BaseBookRegisterRequest request) {
		BookRegisterStrategy strategy = strategyMap.get(request.getClass());
		if (strategy == null) {
			throw new UnsupportedOperationException("Strategy not found for request type: " + request.getClass());
		}
		return strategy;
	}
}
