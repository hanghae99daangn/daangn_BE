package com.sparta.market.global.address.dto;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class AddressResponseDto {
    private String address_name;

    public AddressResponseDto(JSONObject addressJson) {
        this.address_name = addressJson.getString("address_name");
        if (this.address_name.contains("서울")) {
            // 서울이 포함된 경우, 특별시를 서울 바로 뒤에 추가
            int index = this.address_name.indexOf("서울");
            this.address_name = this.address_name.substring(0, index + 2) + "특별시" + this.address_name.substring(index + 2);
        }
    }
}
