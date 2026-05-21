package com.example.springstudy.service;

import com.example.springstudy.dto.MemberDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MemberService {

    public MemberDto.ProfileResponse getProfile(){
        return  MemberDto.ProfileResponse.builder()
                .id(1L)
                .name("김개발")
                .profileImage("/images/profile/dev01.png")
                .supportFields("백엔드, 풀스택")
                .searchTags("Java,Spring,JPA,MySQL,Docker")
                .introduction("5년차 백엔드 개발자입니다. Spring Boot와 클라우드 환경에 강점이 있으며, " +
                        "대용량 트래픽 처리 경험이 있습니다.")
                .isAvailable(true)
                .isOnsiteAvailable(false)
                .regionMain("서울")
                .regionSub("강남구")
                .businessType("프리랜서")
                .careerYear("5년")
                .build();
    }

    public MemberDto.ProfileResponse updateProfile(){
        return MemberDto.ProfileResponse.builder()
                .id(1L)
                .name("김개발")
                .profileImage("/images/profile/dev01.png")
                .supportFields("백엔드, 풀스택")
                .searchTags("Java,Spring,JPA,MySQL,Docker")
                .introduction("프로필이 수정되었습니다.")
                .isAvailable(true)
                .isOnsiteAvailable(false)
                .regionMain("서울")
                .regionSub("강남구")
                .businessType("프리랜서")
                .careerYear("5년")
                .build();
    }

    public MemberDto.ImageUploadResponse uploadImage(MultipartFile image) {
        return MemberDto.ImageUploadResponse.builder()
                .profileImage("/images/profile/dev01_new.png")
                .build();
    }

}
