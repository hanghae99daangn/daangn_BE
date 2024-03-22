package com.sparta.market.global.address.service;

import com.sparta.market.global.address.dto.AddressResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "주소 Service")
@Service
public class AddressService {

    private final RestTemplate restTemplate;

    public AddressService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<AddressResponseDto> searchAddress(String partialAddress){
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/address")
                .queryParam("query", partialAddress)
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .header("Authorization", "KakaoAK 74153e11b9bd504556db7210160ed19d")
                .build();
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        log.info("주소 API Status Code : " + responseEntity.getStatusCode());

        return fromJSONtoItems(responseEntity.getBody());
    }

    public List<AddressResponseDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray addressList  = jsonObject.getJSONArray("documents");
        List<AddressResponseDto> addressDtoList = new ArrayList<>();

        for (Object address : addressList) {
            AddressResponseDto addressDto = new AddressResponseDto((JSONObject) address);
            addressDtoList.add(addressDto);
        }

        return addressDtoList;
    }
}
