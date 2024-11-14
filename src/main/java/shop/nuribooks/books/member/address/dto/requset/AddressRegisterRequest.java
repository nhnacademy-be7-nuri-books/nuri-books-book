package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.member.entity.Member;

@Builder
public record AddressRegisterRequest(
        @NotBlank String name,
        @NotBlank String zipcode,
        @NotBlank String address,
        @NotBlank String detailAddress,
        boolean isDefault) {

    public Address toEntity() {
        return Address.builder()
                .name(name)
                .address(address)
                .detailAddress(detailAddress)
                .build();
    }

}
