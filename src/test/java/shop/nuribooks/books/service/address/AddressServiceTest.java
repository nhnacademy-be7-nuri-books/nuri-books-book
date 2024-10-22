package shop.nuribooks.books.service.address;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.nuribooks.books.dto.address.requset.AddressCreateRequest;
import shop.nuribooks.books.dto.address.requset.AddressEditRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
import shop.nuribooks.books.entity.address.Address;
import shop.nuribooks.books.exception.address.AddressNotFoundException;
import shop.nuribooks.books.repository.address.AddressRepository;

@SpringBootTest
class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressRepository addressRepository;

    @AfterEach
    void tearDown() {
        addressRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 주소를 생성한다.")
    @Test
    void addAddress() {
        // given
        AddressCreateRequest request = AddressCreateRequest.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
                .isDefault(true)
                .build();
        // when
        AddressResponse response = addressService.addAddress(request);

        // then
        assertThat(response.getName()).isEqualTo("test");
    }

    @DisplayName("회원의 주소 리스트를 조회한다.")
    @Test
    void findAddressesByMemberId() {
        // given
        AddressCreateRequest request = AddressCreateRequest.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
                .isDefault(true)
                .build();
        AddressResponse response = addressService.addAddress(request);

        // when
        List<AddressResponse> addressesByMemberId = addressService.findAddressesByMemberId(response.getMemberId());

        // then
        assertThat(addressesByMemberId).hasSize(1);
    }

    @DisplayName("회원의 등록된 주소를 삭제한다.")
    @Test
    void removeAddress() {
        // given
        Address address = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
                .isDefault(true)
                .build();

        Address saved = addressRepository.save(address);
        // when
        addressService.removeAddress(saved.getId());

        // then
        Assertions.assertThat(addressRepository.count()).isEqualTo(0);
    }

    @DisplayName("회원의 등록된 주소를 수정한다.")
    @Test
    void modifyAddress() {
        // given
        Address address = Address.builder()
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
                .isDefault(true)
                .build();

        Address saved = addressRepository.save(address);

        AddressEditRequest addressEditRequest = AddressEditRequest.builder()
                .id(saved.getId())
                .memberId(1L)
                .name("test")
                .address("장말로")
                .address("103호")
                .isDefault(false)
                .build();
        // when
        addressService.modifyAddress(addressEditRequest);

        // then
        Address changedAddress = addressRepository.findById(saved.getId())
                .orElseThrow(() -> new AddressNotFoundException("주소가 없습니다."));
        Assertions.assertThat(changedAddress.isDefault()).isFalse();
    }

}