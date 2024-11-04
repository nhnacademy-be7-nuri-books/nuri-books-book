package shop.nuribooks.books.member.cart.dto;

import lombok.Builder;

@Builder
public record CartAddRequest(

	Long memberId,
	Long bookId,
	int quantity
) {}
