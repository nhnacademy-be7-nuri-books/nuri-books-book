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

    @NotNull
    private Long memberId;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String addressDetail;

    private boolean isDefault;


}
