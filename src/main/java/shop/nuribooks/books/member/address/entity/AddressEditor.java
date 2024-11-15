package shop.nuribooks.books.member.address.entity;

import lombok.Getter;

@Getter
public class AddressEditor {

    private final String name;

    private final String zipcode;

    private final String address;

    private final String detailAddress;

    private final boolean isDefault;

    private AddressEditor(String name, String zipcode, String address, String detailAddress, boolean isDefault) {
        this.name = name;
		this.zipcode = zipcode;
		this.address = address;
        this.detailAddress = detailAddress;
        this.isDefault = isDefault;
    }

    public static AddressEditorBuilder builder() {
        return new AddressEditorBuilder();
    }

    public static class AddressEditorBuilder {
        private String name;
        private String zipcode;
        private String address;
        private String detailAddress;
        private boolean isDefault;

        AddressEditorBuilder() {
        }

        public AddressEditor.AddressEditorBuilder name(final String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder zipcode(final String zipcode) {
            if (zipcode != null) {
                this.zipcode = zipcode;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder address(final String address) {
            if (address != null) {
                this.address = address;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder detailAddress(final String detailAddress) {
            if (detailAddress != null) {
                this.detailAddress = detailAddress;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder isDefault(final boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public AddressEditor build() {
            return new AddressEditor(this.name, this.zipcode, this.address, this.detailAddress, this.isDefault);
        }

    }


}
