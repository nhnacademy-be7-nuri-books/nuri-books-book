package shop.nuribooks.books.member.address.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.common.annotation.HasRole;
import shop.nuribooks.books.common.threadlocal.MemberIdContext;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.service.AddressService;
import shop.nuribooks.books.member.member.entity.AuthorityType;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/api/member/address")
    public ResponseEntity<AddressResponse> addressRegister(@Valid @RequestBody AddressRegisterRequest request) {
        Long memberId = MemberIdContext.getMemberId();
        AddressResponse response = addressService.registerAddress(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 관리자가 회원의 주소들을 조회하는 엔드포인트는 따로 생각해야한다.

    @GetMapping("/api/member/me/address")
    public ResponseEntity<List<AddressResponse>> addressList() {
        Long memberId = MemberIdContext.getMemberId();
        List<AddressResponse> addressesByMemberUsername = addressService.findAddressesByMemberId(memberId);
        return ResponseEntity.ok(addressesByMemberUsername);
    }


    @PatchMapping("/api/member/address/{addressId}")
    public ResponseEntity<AddressResponse> addressModify(@PathVariable Long addressId,
                                                         @Valid @RequestBody AddressEditRequest request) {
        AddressResponse addressResponse = addressService.modifyAddress(addressId, request);
        return ResponseEntity.ok(addressResponse);
    }

    @DeleteMapping("/api/member/address/{addressId}")
    public void addressRemove(@PathVariable Long addressId) {
        addressService.removeAddress(addressId);
    }
}
