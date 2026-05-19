package com.example.springstudy.controller;


import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Application", description = "프로젝트 지원 API")
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Operation(summary = "프로젝트 지원하기")
    @PostMapping
    public ResponseEntity<CommonResponse<ApplicationDto.ApplyResponse>> apply(
            @RequestBody ApplicationDto.ApplyRequest request) {

        ApplicationDto.ApplyResponse dummy = ApplicationDto.ApplyResponse.builder()
                .message("지원이 완료되었습니다.")
                .applicationId(42L)
                .build();

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }

    @Operation(summary = "내가 지원한 프로젝트 목록 (개발자 마이페이지)")
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<ApplicationDto.MyApplicationItem>>> getMyApplications() {

        List<ApplicationDto.MyApplicationItem> dummy = List.of(
                ApplicationDto.MyApplicationItem.builder()
                        .applicationId(42L).projectId(1L)
                        .projectTitle("쇼핑몰 백엔드 API 개발")
                        .status("검토중")
                        .appliedAt(LocalDateTime.of(2026, 5, 10, 14, 30))
                        .bidAmount(250).build(),

                ApplicationDto.MyApplicationItem.builder()
                        .applicationId(38L).projectId(4L)
                        .projectTitle("AWS 기반 인프라 CI/CD 파이프라인 구축")
                        .status("합격")
                        .appliedAt(LocalDateTime.of(2026, 5, 5, 10, 0))
                        .bidAmount(200).build(),

                ApplicationDto.MyApplicationItem.builder()
                        .applicationId(35L).projectId(3L)
                        .projectTitle("공공데이터 수집 배치 시스템 개발")
                        .status("불합격")
                        .appliedAt(LocalDateTime.of(2026, 4, 28, 16, 45))
                        .bidAmount(180).build()
        );

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }

    @Operation(summary = "지원서 상세 조회", description = "제안 내용 내 이메일은 필터링되어 반환됩니다.")
    @GetMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ApplicationDto.ApplicationDetail>> getApplicationDetail(
            @PathVariable Long applicationId) {

        ApplicationDto.ApplicationDetail dummy = ApplicationDto.ApplicationDetail.builder()
                .applicationId(applicationId)
                .projectId(1L)
                .projectTitle("쇼핑몰 백엔드 API 개발")
                .developerName("이자바")
                .workDuration(60)
                .bidAmount(250)
                .proposalContent("해당 프로젝트 경험 다수 보유, 빠른 납기 가능합니다. " +
                        "연락은 플랫폼 메시지로 부탁드립니다. (이메일 노출 필터링됨)")
                .techCategory("백엔드")
                .experienceLevel("중급")
                .headcount(1)
                .appliedAt(LocalDateTime.of(2026, 5, 10, 14, 30))
                .build();

        return ResponseEntity.ok(CommonResponse.ok(dummy));
    }

}
