package com.sparta.market.global.address.service;

import com.sparta.market.global.address.dto.AddressResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j(topic = "주소 Service")
@Service
public class AddressService {

    private final RestTemplate restTemplate;

    public AddressService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<AddressResponseDto> searchAddress(String query){
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/shop.json")
                .queryParam("display", 15)
                .queryParam("query", query)
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);
        return null;
    }
}
