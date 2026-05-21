package com.example.springstudy.controller;

import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.dto.MemberDto;
import com.example.springstudy.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "개발자 프로필 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> getProfile() {

        MemberDto.ProfileResponse response = memberService.getProfile();

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> updateProfile(
            @RequestBody MemberDto.ProfileUpdateRequest request) {

        // 수정된 것처럼 동일한 더미 반환 (실제 request 값 반영 X)
        MemberDto.ProfileResponse response = memberService.updateProfile();

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<MemberDto.ImageUploadResponse>> uploadImage(
            @RequestPart("image") MultipartFile image) {

        MemberDto.ImageUploadResponse response = memberService.uploadImage(image);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}
