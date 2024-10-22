package shop.nuribooks.books.service.address;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.nuribooks.books.dto.address.requset.AddressCreateRequest;
import shop.nuribooks.books.dto.address.response.AddressResponse;
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

}