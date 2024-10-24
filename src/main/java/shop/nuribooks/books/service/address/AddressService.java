package shop.nuribooks.books.service.address;

import java.util.List;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.dto.address.requset.AddressRegisterRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;

public interface AddressService {

    AddressResponse registerAddress(AddressRegisterRequest request);

    List<AddressResponse> findAddressesByMemberId(Long memberId);

    void removeAddress(Long addressId);

    AddressResponse modifyAddress(AddressEditRequest request);

}
