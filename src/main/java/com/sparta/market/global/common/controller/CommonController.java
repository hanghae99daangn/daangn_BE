package com.sparta.market.global.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통 API", description = "HTTPS healthy Check용")
@Slf4j(topic = "HTTPS 검증 Controller")
@RestController
public class CommonController {

    @Operation
    @GetMapping("/")
    public ResponseEntity<?> checkConnection() {
        log.info("HTTPS Connection OK");
        return ResponseEntity.ok().body("HTTPS Connection OK");
    }
}
