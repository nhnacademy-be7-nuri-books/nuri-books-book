package shop.nuribooks.books.member.address.dto.requset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.member.entity.Member;

@Builder
public record AddressRegisterRequest(
        @NotNull Long memberId,
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String addressDetail,
        boolean isDefault) {

    public Address toEntity(Member member) {
        return Address.builder()
                .member(member)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }

}
