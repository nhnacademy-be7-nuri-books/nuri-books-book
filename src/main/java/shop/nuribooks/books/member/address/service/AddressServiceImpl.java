package shop.nuribooks.books.member.address.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.entity.AddressEditor;
import shop.nuribooks.books.member.address.entity.AddressEditor.AddressEditorBuilder;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.exception.member.UserIdNotFoundException;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.member.repository.MemberRepository;

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
                .orElseThrow(AddressNotFoundException::new);
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse modifyAddress(AddressEditRequest request) {
        Address address = addressRepository.findById(request.getId())
                .orElseThrow(AddressNotFoundException::new);

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
