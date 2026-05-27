package com.example.springstudy.service;
import com.example.springstudy.domain.Member;
import com.example.springstudy.dto.AuthDto;
import com.example.springstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    public AuthDto.LoginResponse login(AuthDto.LoginRequest request){

        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다."));

        if (!member.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }

        return AuthDto.LoginResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole().name())
                .build();
    }

}
