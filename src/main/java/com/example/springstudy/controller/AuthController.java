package com.example.springstudy.controller;

import com.example.springstudy.dto.AuthDto;
import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "아이디/비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthDto.LoginResponse>> login(
            @RequestBody AuthDto.LoginRequest request,
            HttpSession session) {

        AuthDto.LoginResponse response = authService.login(request);

        session.setAttribute("loginMemberId", response.getId());
        session.setAttribute("loginMemberRole", response.getRole());

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(CommonResponse.ok("로그아웃 되었습니다."));
    }
}
