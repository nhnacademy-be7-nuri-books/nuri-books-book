package shop.nuribooks.books.book.book.dto;

import java.util.List;

import shop.nuribooks.books.book.bookcontributor.dto.BookContributorInfoResponse;

public record BookContributorsResponse(
	AdminBookListResponse bookDetails,
	List<BookContributorInfoResponse> contributors
) {
}
