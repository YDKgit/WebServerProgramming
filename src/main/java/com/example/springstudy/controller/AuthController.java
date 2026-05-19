package com.example.springstudy.controller;

import com.example.springstudy.dto.AuthDto;
import com.example.springstudy.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthDto.LoginResponse>> login(
            @RequestBody AuthDto.LoginRequest request) {

        AuthDto.LoginResponse dummy = AuthDto.LoginResponse.builder()
                .id(1L)
                .name("김개발")
                .role("DEVELOPER")
                .build();

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }
}
