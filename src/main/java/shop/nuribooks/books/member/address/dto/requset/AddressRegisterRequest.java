package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;

@Builder
public record AddressRegisterRequest(
	@NotBlank String name,
	@NotBlank String zipcode,
	@NotBlank String address,
	@NotBlank String detailAddress,
	boolean isDefault) {

	public Address toEntity(boolean isDefault) {
		return Address.builder()
			.zipcode(zipcode)
		    .name(name)
			.address(address)
			.detailAddress(detailAddress)
			.isDefault(isDefault)
			.build();
	}

}
