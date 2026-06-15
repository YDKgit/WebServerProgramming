package com.example.springstudy.controller;

import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.dto.MemberDto;
import com.example.springstudy.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Tag(name = "Member", description = "개발자 프로필 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> getProfile(HttpSession session) {
        Long memberId = getLoginMemberId(session);
        MemberDto.ProfileResponse response = memberService.getProfile(memberId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "내 프로필 수정")
    @PutMapping("/profile")
    public ResponseEntity<CommonResponse<MemberDto.ProfileResponse>> updateProfile(
            @RequestBody MemberDto.ProfileUpdateRequest request, HttpSession session) {

        Long memberId = getLoginMemberId(session);
        MemberDto.ProfileResponse response = memberService.updateProfile(request, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "프로필 이미지 업로드")
    @PostMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<CommonResponse<MemberDto.ImageUploadResponse>> uploadImage(
            @RequestPart("image") MultipartFile image, HttpSession session) {

        Long memberId = getLoginMemberId(session);
        log.info("프로필 이미지 업로드 API 호출 - memberId={}, fileName={}, size={}bytes",
                memberId, image.getOriginalFilename(), image.getSize());

        MemberDto.ImageUploadResponse response = memberService.uploadImage(image, memberId);
        log.info("프로필 이미지 업로드 API 응답 - memberId={}, profileImage={}",
                memberId, response.getProfileImage());

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    private Long getLoginMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberId;
    }
}
