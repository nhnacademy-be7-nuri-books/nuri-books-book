package shop.nuribooks.books.member.address.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
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
                .address("address")
                .addressDetail("addressDetail")
                .isDefault(false)
                .build();

        AddressEditRequest request = AddressEditRequest.builder()
                .name("name2")
                .address(null)
                .addressDetail(null)
                .isDefault(true)
                .build();

        AddressEditorBuilder addressEditorBuilder = address.toEditor();
        AddressEditor addressEditor = addressEditorBuilder
                .name(request.name())
                .address(request.address())
                .addressDetail(request.addressDetail())
                .isDefault(request.isDefault())
                .build();

        // when
        address.edit(addressEditor);

        // then
        assertThat(address).extracting("name", "address", "addressDetail", "isDefault")
                .containsExactly("name2", "address", "addressDetail", true);
    }

}