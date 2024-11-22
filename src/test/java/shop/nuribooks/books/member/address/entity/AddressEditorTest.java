package shop.nuribooks.books.member.address.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shop.nuribooks.books.member.address.dto.requset.AddressEditRequest;
import shop.nuribooks.books.member.address.entity.AddressEditor.AddressEditorBuilder;

class AddressEditorTest {

	@DisplayName("Editor 테스트")
	@Test
	void test() {
		// given
		Address address = Address.builder()
			.name("name")
			.zipcode("12345")
			.address("address")
			.detailAddress("addressDetail")
			.isDefault(false)
			.build();

		AddressEditRequest request = AddressEditRequest.builder()
			.name("name2")
			.zipcode("12345")
			.address(null)
			.detailAddress(null)
			.build();

		AddressEditorBuilder addressEditorBuilder = address.toEditor();
		AddressEditor addressEditor = addressEditorBuilder
			.name(request.name())
			.zipcode(request.zipcode())
			.address(request.address())
			.detailAddress(request.detailAddress())
			.build();

		// when
		address.edit(addressEditor);

		// then
		assertThat(address).extracting("name", "zipcode", "address", "detailAddress", "isDefault")
			.containsExactly("name2", "12345", "address", "addressDetail", false);
	}

}
