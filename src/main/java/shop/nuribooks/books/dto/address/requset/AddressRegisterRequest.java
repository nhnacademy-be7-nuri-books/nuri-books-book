package shop.nuribooks.books.dto.address.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.entity.address.Address;

@Getter
@NoArgsConstructor
public class AddressRegisterRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String addressDetail;

    private boolean isDefault;

    @Builder
    private AddressRegisterRequest(Long memberId, String name, String address, String addressDetail, boolean isDefault) {
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public Address toEntity() {
        return Address.builder()
                .memberId(memberId)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }

}
