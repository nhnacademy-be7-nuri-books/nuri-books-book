package shop.nuribooks.books.member.address.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.exception.address.AddressOverMaximumSizeException;
import shop.nuribooks.books.exception.member.MemberNotFoundException;
import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.dto.requset.AddressRegisterRequest;
import shop.nuribooks.books.member.address.dto.response.AddressResponse;
import shop.nuribooks.books.member.address.entity.Address;
import shop.nuribooks.books.member.address.entity.AddressEditor;
import shop.nuribooks.books.member.address.entity.AddressEditor.AddressEditorBuilder;
import shop.nuribooks.books.member.address.repository.AddressRepository;
import shop.nuribooks.books.member.member.entity.Member;
import shop.nuribooks.books.member.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
	private static final int ADDRESS_MAXIMUM_SIZE = 10;

	private final AddressRepository addressRepository;
	private final MemberRepository memberRepository;

	private static AddressEditor getAddressEditor(AddressEditRequest request, Address address) {
		AddressEditorBuilder addressEditorBuilder = address.toEditor();
		return addressEditorBuilder
			.name(request.name())
			.zipcode(request.zipcode())
			.address(request.address())
			.detailAddress(request.detailAddress())
			.build();
	}

	@Transactional
	public AddressResponse registerAddress(AddressRegisterRequest request, Long memberId) {
		int size = addressRepository.findAllByMemberId(memberId).size();
		if (size >= ADDRESS_MAXIMUM_SIZE) {
			throw new AddressOverMaximumSizeException("주소는 최대 10개 등록 가능합니다");
		}

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

		boolean isDefault = getIsDefault(size);
		Address address = request.toEntity(isDefault);
		member.addAddress(address);
		return AddressResponse.of(address);
	}

	private boolean getIsDefault(int size) {
		return size == 0;
	}

	@Transactional(readOnly = true)
	public List<AddressResponse> findAddressesByMemberId(Long memberId) {
		List<Address> addressesByMemberId = addressRepository.findAllByMemberId(memberId);
		return addressesByMemberId.stream()
			.map(AddressResponse::of)
			.toList();
	}

	@Transactional
	public void removeAddress(Long addressId) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));
		addressRepository.delete(address);
	}

	@Transactional
	public AddressResponse modifyAddress(Long addressId, AddressEditRequest request) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new AddressNotFoundException("주소를 찾을 수 없습니다."));

		AddressEditor addressEditor = getAddressEditor(request, address);
		address.edit(addressEditor);
		return AddressResponse.of(address);
	}

}
