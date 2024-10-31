package shop.nuribooks.books.book.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.nuribooks.books.book.book.dto.BookResponse;

@FeignClient(name = "aladinClient", url = "http://api.aladin.co.kr")
public interface AladinFeginClient {

	@GetMapping("/ttb/api/ItemSearch.aspx")
	BookResponse getNewBooks(
		@RequestParam("ttbkey") String ttbKey,
		@RequestParam(value = "QueryType", defaultValue = "ItemNewAll") String queryType,
		@RequestParam(value = "MaxResults", defaultValue = "10") int maxResults,
		@RequestParam(value = "start", defaultValue = "1") int startIndex,
		@RequestParam(value = "SearchTarget", defaultValue = "Book") String searchTarget,
		@RequestParam(value = "output", defaultValue = "JS") String output
	);

	@GetMapping("/ttb/api/ItemLookUp.aspx")
	BookResponse getBookDetails(
		@RequestParam("ttbkey") String ttbKey,
		@RequestParam("itemIdType") String itemIdType,
		@RequestParam("ItemId") String itemId,
		@RequestParam("output") String output
	);
}
