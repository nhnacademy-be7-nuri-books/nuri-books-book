package shop.nuribooks.books.entity.address;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String name;

    private String address;

    private String addressDetail;

    private boolean isDefault;

    @Builder
    private Address(Long memberId, String name, String address, String addressDetail, boolean isDefault) {
        this.memberId = memberId;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public AddressEditor.AddressEditorBuilder toEditor() {
        return AddressEditor.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .isDefault(isDefault);

    }

    public void edit(AddressEditor addressEditor) {
        name = addressEditor.getName();
        address = addressEditor.getAddress();
        addressDetail = addressEditor.getAddressDetail();
        isDefault = addressEditor.isDefault();
    }
}
