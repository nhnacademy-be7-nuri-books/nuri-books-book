package shop.nuribooks.books.service.address;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.nuribooks.books.dto.address.requset.AddressCreateRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.entity.address.Address;
import shop.nuribooks.books.repository.address.AddressRepository;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressResponse addAddress(AddressCreateRequest request) {
        //TODO: 회원 주소가 10개 넘어가는 경우 예외처리
        Address address = request.toEntity();
        Address saved = addressRepository.save(address);
        return AddressResponse.of(saved);
    }

    public List<AddressResponse> findAddressesByMemberId(Long memberId) {
        //TODO: 주소 없는 경우 예외처리
        List<Address> addressesByMemberId = addressRepository.findAllByMemberId(memberId);
        return addressesByMemberId.stream()
                .map(AddressResponse::of)
                .toList();
    }


}
