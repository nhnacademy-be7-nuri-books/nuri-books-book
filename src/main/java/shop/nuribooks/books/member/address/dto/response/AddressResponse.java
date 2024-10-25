package shop.nuribooks.books.member.address.dto.response;

import lombok.Builder;
import lombok.Getter;
import shop.nuribooks.books.member.address.entity.Address;

@Getter
public class AddressResponse {

    private Long id;
    private String name;
    private String address;
    private String addressDetail;
    private boolean isDefault;

    @Builder
    private AddressResponse(Long id, String name, String address, String addressDetail,
                           boolean isDefault) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

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
