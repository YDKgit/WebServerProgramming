package com.example.springstudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AuthDto {

    @Getter
    @Schema(description = "로그인 요청")
    public static class LoginRequest {
        @Schema(description = "로그인 아이디", example = "dev01")
        private String loginId;
        @Schema(description = "비밀번호", example = "password123")
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "로그인 응답")
    public static class LoginResponse {
        @Schema(description = "회원 PK", example = "1")
        private Long id;
        @Schema(description = "이름", example = "김개발")
        private String name;
        @Schema(description = "역할", example = "DEVELOPER")
        private String role;
    }
}
