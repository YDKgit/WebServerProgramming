package com.example.springstudy.controller;


import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Application", description = "프로젝트 지원 API")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;


    @Operation(summary = "프로젝트 지원하기")
    @PostMapping
    public ResponseEntity<?> apply(
            @RequestBody ApplicationDto.ApplyRequest request, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if(role == null || !role.equals("DEVELOPER")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        ApplicationDto.ApplyResponse response = applicationService.apply(request, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }


    @Operation(summary = "내가 지원한 프로젝트 목록 (개발자 마이페이지)")
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if(role == null || !role.equals("DEVELOPER")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        List<ApplicationDto.MyApplicationItem> response = applicationService.getMyApplications(memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }


    @Operation(summary = "지원서 상세 조회", description = "제안 내용 내 이메일은 필터링되어 반환됩니다.")
    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplicationDetail(
            @PathVariable Long applicationId, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if(role == null || !role.equals("DEVELOPER")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        ApplicationDto.ApplicationDetail response = applicationService.getApplicationDetail(applicationId, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

}
