package com.example.springstudy.controller;

import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "개발자 프로필 API")
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> getProfile() {

        MemberDto.ProfileResponse dummy = MemberDto.ProfileResponse.builder()
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

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> updateProfile(
            @RequestBody MemberDto.ProfileUpdateRequest request) {

        // 수정된 것처럼 동일한 더미 반환 (실제 request 값 반영 X)
        MemberDto.ProfileResponse dummy = MemberDto.ProfileResponse.builder()
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

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<MemberDto.ImageUploadResponse>> uploadImage(
            @RequestPart("image") MultipartFile image) {

        MemberDto.ImageUploadResponse dummy = MemberDto.ImageUploadResponse.builder()
                .profileImage("/images/profile/dev01_new.png")
                .build();

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }
}
