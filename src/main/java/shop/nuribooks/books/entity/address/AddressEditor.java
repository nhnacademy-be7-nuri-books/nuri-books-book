package shop.nuribooks.books.entity.address;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AddressEditor {

    private String name;

    private String address;

    private String addressDetail;

    private boolean isDefault;

    @Builder
    private AddressEditor(String name, String address, String addressDetail, boolean isDefault) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }



}
