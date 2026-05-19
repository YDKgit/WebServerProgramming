package com.example.springstudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ApplicationDto {

    @Getter
    @Schema(description = "프로젝트 지원 요청")
    public static class ApplyRequest {
        @Schema(description = "프로젝트 ID", example = "1")
        private Long projectId;
        @Schema(description = "작업 기간(일)", example = "60")
        private Integer workDuration;
        @Schema(description = "희망 단가(만원)", example = "250")
        private Integer bidAmount;
        @Schema(description = "제안 내용", example = "해당 프로젝트 경험 다수 보유, 빠른 납기 가능합니다.")
        private String proposalContent;
        @Schema(description = "기술 카테고리", example = "백엔드")
        private String techCategory;
        @Schema(description = "경력 수준", example = "중급")
        private String experienceLevel;
        @Schema(description = "투입 인원", example = "1")
        private Integer headcount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "지원 결과 응답")
    public static class ApplyResponse {
        @Schema(description = "결과 메시지", example = "지원이 완료되었습니다.")
        private String message;
        @Schema(description = "지원 ID", example = "42")
        private Long applicationId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "내 지원 내역 아이템")
    public static class MyApplicationItem {
        @Schema(description = "지원 ID", example = "42")
        private Long applicationId;
        @Schema(description = "프로젝트 ID", example = "1")
        private Long projectId;
        @Schema(description = "프로젝트 제목", example = "쇼핑몰 백엔드 API 개발")
        private String projectTitle;
        @Schema(description = "지원 상태", example = "검토중")
        private String status;
        @Schema(description = "지원 일시", example = "2026-05-10T14:30:00")
        private LocalDateTime appliedAt;
        @Schema(description = "희망 단가(만원)", example = "250")
        private Integer bidAmount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "지원자 목록 아이템 (의뢰인용)")
    public static class ApplicantItem {
        @Schema(description = "지원 ID", example = "42")
        private Long applicationId;
        @Schema(description = "개발자 ID", example = "7")
        private Long developerId;
        @Schema(description = "개발자 이름", example = "이자바")
        private String developerName;
        @Schema(description = "프로필 이미지", example = "/images/profile/dev07.png")
        private String profileImage;
        @Schema(description = "경력 수준", example = "고급")
        private String experienceLevel;
        @Schema(description = "기술 카테고리", example = "백엔드")
        private String techCategory;
        @Schema(description = "희망 단가(만원)", example = "350")
        private Integer bidAmount;
        @Schema(description = "제안 내용 요약", example = "유사 프로젝트 3건 완료, 즉시 투입 가능")
        private String proposalSummary;
        @Schema(description = "지원 일시", example = "2026-05-12T09:00:00")
        private LocalDateTime appliedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "지원서 상세 응답")
    public static class ApplicationDetail {
        @Schema(description = "지원 ID", example = "42")
        private Long applicationId;
        @Schema(description = "프로젝트 ID", example = "1")
        private Long projectId;
        @Schema(description = "프로젝트 제목", example = "쇼핑몰 백엔드 API 개발")
        private String projectTitle;
        @Schema(description = "개발자 이름", example = "이자바")
        private String developerName;
        @Schema(description = "작업 기간(일)", example = "60")
        private Integer workDuration;
        @Schema(description = "희망 단가(만원)", example = "250")
        private Integer bidAmount;
        @Schema(description = "제안 내용 (이메일 필터링됨)", example = "해당 프로젝트 경험 다수 보유, 빠른 납기 가능합니다.")
        private String proposalContent;
        @Schema(description = "기술 카테고리", example = "백엔드")
        private String techCategory;
        @Schema(description = "경력 수준", example = "중급")
        private String experienceLevel;
        @Schema(description = "투입 인원", example = "1")
        private Integer headcount;
        @Schema(description = "지원 일시", example = "2026-05-10T14:30:00")
        private LocalDateTime appliedAt;
    }
}
