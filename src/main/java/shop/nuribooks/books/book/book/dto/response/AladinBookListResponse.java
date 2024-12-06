package shop.nuribooks.books.book.book.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AladinBookListResponse(
	@JsonProperty("item") List<AladinBookListItemResponse> item
) {
}