package shop.nuribooks.books.member.address.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;

@Builder
public record AddressResponse(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String addressDetail,
        boolean isDefault) {

    public static AddressResponse of(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .name(address.getName())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .isDefault(address.isDefault())
                .build();
    }
}
