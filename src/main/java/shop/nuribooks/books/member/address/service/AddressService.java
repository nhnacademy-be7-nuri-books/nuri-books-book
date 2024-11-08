package shop.nuribooks.books.member.address.service;

import java.util.List;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;

public interface AddressService {

    AddressResponse registerAddress(AddressRegisterRequest request, Long memberId);

    List<AddressResponse> findAddressesByMemberId(Long memberId);

    void removeAddress(Long addressId);

    AddressResponse modifyAddress(Long addressId, AddressEditRequest request);

}
