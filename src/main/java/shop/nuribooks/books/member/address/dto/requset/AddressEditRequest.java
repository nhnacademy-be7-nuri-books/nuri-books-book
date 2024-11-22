package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressEditRequest(
	@NotBlank String name,
	@NotBlank String zipcode,
	@NotBlank String address,
	@NotBlank String detailAddress) {
}
