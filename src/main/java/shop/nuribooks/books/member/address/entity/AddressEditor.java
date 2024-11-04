package shop.nuribooks.books.member.address.entity;

import lombok.Getter;

@Getter
public class AddressEditor {

    private final String name;

    private final String address;

    private final String addressDetail;

    private final boolean isDefault;

    private AddressEditor(String name, String address, String addressDetail, boolean isDefault) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public static AddressEditorBuilder builder() {
        return new AddressEditorBuilder();
    }

    public static class AddressEditorBuilder {
        private String name;
        private String address;
        private String addressDetail;
        private boolean isDefault;

        AddressEditorBuilder() {
        }

        public AddressEditor.AddressEditorBuilder name(final String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder address(final String address) {
            if (address != null) {
                this.address = address;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder addressDetail(final String addressDetail) {
            if (addressDetail != null) {
                this.addressDetail = addressDetail;
            }
            return this;
        }

        public AddressEditor.AddressEditorBuilder isDefault(final boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public AddressEditor build() {
            return new AddressEditor(this.name, this.address, this.addressDetail, this.isDefault);
        }

    }


}
