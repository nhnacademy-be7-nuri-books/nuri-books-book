package shop.nuribooks.books.member.address.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;

@Builder
public record AddressResponse(
	Long id,
	String name,
	String zipcode,
	String address,
	String detailAddress,
	boolean isDefault) {

	public static AddressResponse of(Address address) {
		return AddressResponse.builder()
			.id(address.getId())
			.name(address.getName())
			.zipcode(address.getZipcode())
			.address(address.getAddress())
			.detailAddress(address.getDetailAddress())
			.isDefault(address.isDefault())
			.build();
	}
}
