package shop.nuribooks.books.controller.address;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<AddressResponse> addressAdd(@PathVariable Long memberId,
                                                         @RequestBody AddressCreateRequest request) {
        AddressResponse response = addressService.addAddress(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/member/{memberId}/address")
    public ResponseEntity<List<AddressResponse>> addressList(@PathVariable Long memberId) {
        List<AddressResponse> addressesByMemberId = addressService.findAddressesByMemberId(memberId);
        return ResponseEntity.ok(addressesByMemberId);
    }
}
