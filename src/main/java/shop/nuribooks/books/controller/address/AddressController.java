package shop.nuribooks.books.controller.address;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.nuribooks.books.dto.address.requset.AddressCreateRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.service.address.AddressService;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/api/member/{memberId}/address")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Long memberId,
                                                         @RequestBody AddressCreateRequest request) {
        AddressResponse response = addressService.createAddress(request);
        return ResponseEntity.ok(response);
    }
}
