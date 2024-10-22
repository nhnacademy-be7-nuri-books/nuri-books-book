package shop.nuribooks.books.service.address;

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

    public AddressResponse createAddress(AddressCreateRequest request) {
        Address address = request.toEntity();
        Address saved = addressRepository.save(address);
        return AddressResponse.of(saved);
    }

}
