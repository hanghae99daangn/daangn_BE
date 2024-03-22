package com.sparta.market.global.address.dto;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class AddressResponseDto {
    private String address_name;

    public AddressResponseDto(JSONObject addressJson) {
        this.address_name = addressJson.getString("address_name");
    }
}
