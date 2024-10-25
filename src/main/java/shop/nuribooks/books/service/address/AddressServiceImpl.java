package shop.nuribooks.books.service.address;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.dto.address.requset.AddressRegisterRequest;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.entity.address.Address;
import shop.nuribooks.books.entity.address.AddressEditor;
import shop.nuribooks.books.entity.address.AddressEditor.AddressEditorBuilder;
import shop.nuribooks.books.entity.member.Member;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.repository.address.AddressRepository;
import shop.nuribooks.books.repository.member.MemberRepository;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService{

    private final MemberRepository memberRepository;

    private final AddressRepository addressRepository;


    public AddressResponse registerAddress(AddressRegisterRequest request) {
        //TODO: 회원 주소가 10개 넘어가는 경우 예외처리
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new UserIdNotFoundException("유저를 찾을 수 없습니다."));

        Address address = request.toEntity(member);
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

    public void removeAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse modifyAddress(AddressEditRequest request) {
        Address address = addressRepository.findById(request.getId())
                .orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));

        AddressEditor addressEditor = getAddressEditor(request, address);
        address.edit(addressEditor);
        return AddressResponse.of(address);
    }

    private static AddressEditor getAddressEditor(AddressEditRequest request, Address address) {
        AddressEditorBuilder addressEditorBuilder = address.toEditor();
        return addressEditorBuilder
                .name(request.getName())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .isDefault(request.isDefault())
                .build();
    }


}
