package shop.nuribooks.books.member.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.member.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private Member member;

    private String name;

    private String address;

    private String addressDetail;

    private boolean isDefault;

    @Builder
    private Address(String name, String address, String addressDetail, boolean isDefault) {
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
