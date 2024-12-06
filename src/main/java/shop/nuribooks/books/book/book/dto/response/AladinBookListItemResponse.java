package shop.nuribooks.books.book.book.dto.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AladinBookListItemResponse(
	@JsonProperty("title") String title,
	@JsonProperty("author") String author,
	@JsonProperty("pubDate") String pubDate,
	@JsonProperty("description") String description,
	@JsonProperty("isbn") String isbn,
	@JsonProperty("priceStandard") BigDecimal priceStandard, //정가
	@JsonProperty("stockStatus") String stockStatus, //재고상태(정상유통일 경우 비어있음, 품절/절판 등)
	@JsonProperty("cover") String cover,
	@JsonProperty("publisher") String publisher,
	@JsonProperty("categoryName") String categoryName
) {
}
