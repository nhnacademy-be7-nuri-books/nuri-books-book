package shop.nuribooks.books.book.book.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AladinBookListItemResponse(
	@JsonProperty("title") String title,
	//@JsonProperty("link") String link,
	@JsonProperty("author") String author,
	@JsonProperty("pubDate") String pubDate,
	@JsonProperty("description") String description,
	@JsonProperty("isbn") String isbn,
	//@JsonProperty("isbn13") String isbn13,
	//@JsonProperty("priceSales") BigDecimal priceSales, //판매가
	@JsonProperty("priceStandard") BigDecimal priceStandard, //정가
	//@JsonProperty("mallType") String mallType,
	@JsonProperty("stockStatus") String stockStatus, //재고상태(정상유통일 경우 비어있음, 품절/절판 등)
	//@JsonProperty("mileage") int mileage,
	@JsonProperty("cover") String cover,
	@JsonProperty("publisher") String publisher,
	@JsonProperty("categoryName") String categoryName
	//@JsonProperty("salesPoint") int salesPoint, //판매지수
	//@JsonProperty("adult") boolean adult, //성인등급여부
	//@JsonProperty("fixedPrice") boolean fixedPrice, //(종이책/전자책인 경우) 정가제 여부 (true인 경우 정가제 해당 도서)
	//@JsonProperty("customerReviewRank") int customerReviewRank, //평점
	//@JsonProperty("bestRank") Integer bestRank //베스트셀러 순위
) {
}
