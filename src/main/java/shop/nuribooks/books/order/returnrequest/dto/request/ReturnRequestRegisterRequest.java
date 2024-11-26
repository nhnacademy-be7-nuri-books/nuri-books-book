package shop.nuribooks.books.order.returnrequest.dto.request;

import jakarta.validation.constraints.NotBlank;
import shop.nuribooks.books.order.order.entity.Order;
import shop.nuribooks.books.order.returnrequest.entity.ReturnRequest;

public record ReturnRequestRegisterRequest(@NotBlank(message = "반품 요청 내용은 필수입니다.")
										   String contents,
										   @NotBlank(message = "반품 요청 이미지는 필수입니다.")
										   String imageUrl) {
	public ReturnRequest toEntity(Order order) {
		return ReturnRequest.builder()
			.contents(contents)
			.imageUrl(imageUrl)
			.order(order)
			.build();
	}
}
