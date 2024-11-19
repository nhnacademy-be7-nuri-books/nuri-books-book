package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record AddressEditRequest(
        @NotBlank String name,
		@NotBlank String zipcode,
        @NotBlank String address,
        @NotBlank String detailAddress,
        boolean isDefault) {
}
