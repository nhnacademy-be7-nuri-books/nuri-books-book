package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
