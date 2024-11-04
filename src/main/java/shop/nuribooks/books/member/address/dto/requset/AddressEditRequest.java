package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record AddressEditRequest(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String addressDetail,
        boolean isDefault) {
}
