package com.example.springstudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ProjectDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "프로젝트 목록 아이템")
    public static class ProjectSummary {
        @Schema(description = "프로젝트 PK", example = "1")
        private Long id;
        @Schema(description = "프로젝트 제목", example = "쇼핑몰 백엔드 API 개발")
        private String title;
        @Schema(description = "기술 스택", example = "Java, Spring Boot, MySQL")
        private String techStack;
        @Schema(description = "예산(만원)", example = "300")
        private Integer budget;
        @Schema(description = "모집 상태", example = "모집중")
        private String recruitStatus;
        @Schema(description = "모집 마감일", example = "2026-06-30")
        private LocalDate deadline;
        @Schema(description = "카테고리", example = "백엔드")
        private String category;
        @Schema(description = "참여파트", example = "기획,개발")
        private String participationFields;
        @Schema(description = "프로젝트 설명")
        private String description;
        @Schema(description = "미팅 지역", example = "서울")
        private String meetingRegion;
        @Schema(description = "고용형태", example = "도급")
        private String employmentType;
        @Schema(description = "예상 기간(개월)", example = "3")
        private Integer estimatedDuration;
        @Schema(description = "현재 지원자 수", example = "7")
        private Integer applicantCount;
        @Schema(description = "프로젝트 작성자 ID", example = "4")
        private Long clientId;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "프로젝트 상세")
    public static class ProjectDetail {
        @Schema(description = "프로젝트 PK", example = "1")
        private Long id;
        @Schema(description = "제목", example = "쇼핑몰 백엔드 API 개발")
        private String title;
        @Schema(description = "업무 내용", example = "상품/주문/결제 REST API 설계 및 개발, 외부 PG사 연동 포함")
        private String workContent;
        @Schema(description = "필요 기술", example = "Java 17, Spring Boot 3.x, JPA, MySQL 8.0")
        private String requiredSkills;
        @Schema(description = "참여파트", example = "기획,개발")
        private String participationFields;
        @Schema(description = "예상 기간(개월)", example = "3")
        private Integer estimatedDuration;
        @Schema(description = "예산(만원)", example = "300")
        private Integer budget;
        @Schema(description = "모집 상태", example = "모집중")
        private String recruitStatus;
        @Schema(description = "모집 마감일", example = "2026-06-30")
        private LocalDate deadline;
        @Schema(description = "근무 방식", example = "원격")
        private String workType;
        @Schema(description = "고용형태", example = "도급")
        private String employmentType;
        @Schema(description = "시작 예정일", example = "2026-07-10")
        private LocalDate startDate;
        @Schema(description = "의뢰인 이름", example = "박클라이언트")
        private String clientName;
        @Schema(description = "현재 지원자 수", example = "7")
        private Integer applicantCount;
        @Schema(description = "프로젝트 작성자 ID", example = "4")
        private Long clientId;
    }
    @Getter
    @Schema(description = "프로젝트 등록 요청")
    public static class ProjectCreateRequest {
        @Schema(description = "프로젝트명", example = "쇼핑몰 백엔드 API 개발")
        private String title;
        @Schema(description = "모집 마감일", example = "2026-06-30")
        private LocalDate deadline;
        @Schema(description = "고용형태", example = "도급")
        private String employmentType;
        @Schema(description = "예산(만원)", example = "300")
        private Integer budget;
        @Schema(description = "업무 내용", example = "상품/주문/결제 REST API 설계 및 개발")
        private String workContent;
        @Schema(description = "필요 기술", example = "Java 17, Spring Boot 3.x, JPA")
        private String requiredSkills;
        @Schema(description = "참여파트", example = "[\"기획\", \"개발\"]")
        private List<String> participationFields;
        @Schema(description = "예상 기간(개월)", example = "3")
        private Integer estimatedDuration;
        @Schema(description = "근무 방식", example = "원격")
        private String workType;
        @Schema(description = "시작 예정일", example = "2026-07-10")
        private LocalDate startDate;
    }
    @Getter
    @Schema(description = "프로젝트 수정 요청")
    public static class ProjectUpdateRequest {
        private String title;
        private LocalDate deadline;
        private String employmentType;
        private Integer budget;
        private String workContent;
        private String requiredSkills;
        private List<String> participationFields;
        private Integer estimatedDuration;
        private String workType;
        private LocalDate startDate;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "프로젝트 등록 응답")
    public static class ProjectCreateResponse {
        @Schema(description = "생성된 프로젝트 ID", example = "10")
        private Long id;
        @Schema(description = "결과 메시지", example = "프로젝트가 등록되었습니다.")
        private String message;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "페이지네이션 응답 래퍼")
    public static class PageResponse<T> {
        private List<T> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}
