package shop.nuribooks.books.dto.address.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.nuribooks.books.entity.address.Address;

@Getter
@NoArgsConstructor
public class AddressEditRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String addressDetail;

    private boolean isDefault;

    @Builder
    private AddressEditRequest(Long id, String name, String address, String addressDetail, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

}
