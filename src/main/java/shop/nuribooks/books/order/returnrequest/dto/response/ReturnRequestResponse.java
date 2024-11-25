package shop.nuribooks.books.order.returnrequest.dto.response;

import lombok.Builder;
import shop.nuribooks.books.order.returnrequest.entity.ReturnRequest;

@Builder
public record ReturnRequestResponse(
	Long id,
	String contents,
	String imageUrl
) {
	public static ReturnRequestResponse of(ReturnRequest returnRequest) {
		return ReturnRequestResponse.builder()
			.id(returnRequest.getId())
			.contents(returnRequest.getContents())
			.imageUrl(returnRequest.getImageUrl())
			.build();
	}
}
