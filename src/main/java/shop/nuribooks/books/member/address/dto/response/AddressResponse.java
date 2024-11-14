package shop.nuribooks.books.member.address.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;

@Builder
public record AddressResponse(
        @NotBlank String name,
        @NotBlank String zipcode,
        @NotBlank String address,
        @NotBlank String addressDetail,
        boolean isDefault) {

    public static AddressResponse of(Address address) {
        return AddressResponse.builder()
                .name(address.getName())
                .address(address.getAddress())
                .addressDetail(address.getDetailAddress())
                .isDefault(address.isDefault())
                .build();
    }
}
