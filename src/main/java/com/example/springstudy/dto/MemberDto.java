package com.example.springstudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "개발자 프로필 응답")
    public static class ProfileResponse {
        @Schema(description = "회원 PK", example = "1")
        private Long id;
        @Schema(description = "이름", example = "김개발")
        private String name;
        @Schema(description = "프로필 이미지 경로", example = "/images/profile/dev01.png")
        private String profileImage;
        @Schema(description = "지원 분야", example = "백엔드, 풀스택")
        private String supportFields;
        @Schema(description = "검색 태그 (5개)", example = "Java,Spring,JPA,MySQL,Docker")
        private String searchTags;
        @Schema(description = "자기소개", example = "5년차 백엔드 개발자입니다. Spring Boot와 클라우드 환경에 강점이 있습니다.")
        private String introduction;
        @Schema(description = "재택 가능 여부", example = "true")
        private Boolean isAvailable;
        @Schema(description = "상주 가능 여부", example = "false")
        private Boolean isOnsiteAvailable;
        @Schema(description = "주 활동 지역(광역)", example = "서울")
        private String regionMain;
        @Schema(description = "주 활동 지역(시군구)", example = "강남구")
        private String regionSub;
        @Schema(description = "사업자 유형", example = "프리랜서")
        private String businessType;
        @Schema(description = "경력 연차", example = "5년")
        private String careerYear;
    }

    @Getter
    @Schema(description = "개발자 프로필 수정 요청")
    public static class ProfileUpdateRequest {
        private String supportFields;
        private String searchTags;
        private String introduction;
        private Boolean isAvailable;
        private Boolean isOnsiteAvailable;
        private String regionMain;
        private String regionSub;
        private String businessType;
        private String careerYear;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "이미지 업로드 응답")
    public static class ImageUploadResponse {
        @Schema(description = "업로드된 이미지 경로", example = "/images/profile/dev01_new.png")
        private String profileImage;
    }
}
