package com.sparta.market.global.address.controller;

import com.sparta.market.global.address.dto.AddressResponseDto;
import com.sparta.market.global.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "카카오 주소 Rest API", description = "주소 검색")
@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "XX동 검색")
    @GetMapping("/search")
    public List<AddressResponseDto> searchAddress(@RequestParam String partialAddress) {
        return addressService.searchAddress(partialAddress);
    }
}
