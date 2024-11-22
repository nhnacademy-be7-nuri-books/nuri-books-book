package shop.nuribooks.books.member.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.nuribooks.books.member.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "addresses")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@Setter
	private Member member;

	private String name;

	private String zipcode;

	private String address;

	private String detailAddress;

	private boolean isDefault;

	@Builder
	private Address(String name, String zipcode, String address, String detailAddress, boolean isDefault) {
		this.name = name;
		this.zipcode = zipcode;
		this.address = address;
		this.detailAddress = detailAddress;
		this.isDefault = isDefault;
	}

	public AddressEditor.AddressEditorBuilder toEditor() {
		return AddressEditor.builder()
			.name(name)
			.zipcode(zipcode)
			.address(address)
			.detailAddress(detailAddress);
	}

	public void edit(AddressEditor addressEditor) {
		name = addressEditor.getName();
		zipcode = addressEditor.getZipcode();
		address = addressEditor.getAddress();
		detailAddress = addressEditor.getDetailAddress();
	}

}
