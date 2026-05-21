package com.example.springstudy.service;
import com.example.springstudy.dto.AuthDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request){
        return AuthDto.LoginResponse.builder()
                .id(1L)
                .name("김개발")
                .role("DEVELOPER")
                .build();
    }

}
