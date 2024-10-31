package shop.nuribooks.books.member.address.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.service.AddressService;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/api/member/{memberId}/address")
    public ResponseEntity<AddressResponse> addressRegister(@PathVariable Long memberId,
                                                      @Valid @RequestBody AddressRegisterRequest request) {
        AddressResponse response = addressService.registerAddress(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/member/{memberId}/address")
    public ResponseEntity<List<AddressResponse>> addressList(@PathVariable Long memberId) {
        List<AddressResponse> addressesByMemberId = addressService.findAddressesByMemberId(memberId);
        return ResponseEntity.ok(addressesByMemberId);
    }

    @PatchMapping("/api/member/{memberId}/address/{addressId}")
    public ResponseEntity<AddressResponse> addressModify(@PathVariable Long memberId,
                                                         @PathVariable Long addressId,
                                                         @Valid @RequestBody AddressEditRequest request) {
        AddressResponse addressResponse = addressService.modifyAddress(request);
        return ResponseEntity.ok(addressResponse);
    }

    @DeleteMapping("/api/member/{memberId}/address/{addressId}")
    public void addressRemove(@PathVariable Long memberId,
                              @PathVariable Long addressId) {
        addressService.removeAddress(addressId);
    }
}
