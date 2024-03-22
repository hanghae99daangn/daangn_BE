package com.sparta.market.global.address.controller;

import com.sparta.market.global.address.dto.AddressResponseDto;
import com.sparta.market.global.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/search")
    public List<AddressResponseDto> searchAddress(@RequestParam String query) {
        return addressService.searchAddress(query);
    }
}
