package shop.nuribooks.books.dto.address.response;

import lombok.Builder;
import lombok.Getter;
import shop.nuribooks.books.entity.address.Address;

@Getter
public class AddressResponse {

    private Long id;
    private Long memberId;
    private String name;
    private String address;
    private String addressDetail;
    private boolean isDefault;

    @Builder
    private AddressResponse(Long id, Long memberId, String name, String address, String addressDetail,
                           boolean isDefault) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public static AddressResponse of(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .memberId(address.getMemberId())
                .name(address.getName())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .isDefault(address.isDefault())
                .build();
    }
}