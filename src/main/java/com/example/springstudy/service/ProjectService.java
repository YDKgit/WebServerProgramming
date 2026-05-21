package com.example.springstudy.service;

import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.ProjectDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    public ProjectDto.ProjectCreateResponse createProject(ProjectDto.ProjectCreateRequest request) {
        return ProjectDto.ProjectCreateResponse.builder()
                .id(10L)
                .message("프로젝트가 등록되었습니다.")
                .build();
    }

    public ProjectDto.PageResponse<ProjectDto.ProjectSummary> getProjects(String keyword, Pageable pageable) {
        List<ProjectDto.ProjectSummary> content = List.of(
                ProjectDto.ProjectSummary.builder()
                        .id(1L).title("쇼핑몰 백엔드 API 개발")
                        .techStack("Java, Spring Boot, MySQL")
                        .budget(300).recruitStatus("모집중")
                        .deadline(LocalDate.of(2026, 6, 30))
                        .category("백엔드")
                        .employmentType("도급")
                        .estimatedDuration(3)
                        .applicantCount(7)
                        .build(),

                ProjectDto.ProjectSummary.builder()
                        .id(2L).title("React 기반 관리자 대시보드 UI 구축")
                        .techStack("React, TypeScript, Tailwind CSS")
                        .budget(200).recruitStatus("모집중")
                        .deadline(LocalDate.of(2026, 6, 15))
                        .category("프론트엔드")
                        .employmentType("상주")
                        .estimatedDuration(2)
                        .applicantCount(3)
                        .build(),

                ProjectDto.ProjectSummary.builder()
                        .id(3L).title("공공데이터 수집 배치 시스템 개발")
                        .techStack("Python, FastAPI, PostgreSQL")
                        .budget(150).recruitStatus("모집마감")
                        .deadline(LocalDate.of(2026, 5, 31))
                        .category("백엔드")
                        .employmentType("도급")
                        .estimatedDuration(1)
                        .applicantCount(12)
                        .build(),

                ProjectDto.ProjectSummary.builder()
                        .id(4L).title("AWS 기반 인프라 CI/CD 파이프라인 구축")
                        .techStack("AWS, Docker, GitHub Actions, Terraform")
                        .budget(250).recruitStatus("모집중")
                        .deadline(LocalDate.of(2026, 7, 10))
                        .category("DevOps")
                        .employmentType("도급")
                        .estimatedDuration(2)
                        .applicantCount(5)
                        .build()
        );

        return ProjectDto.PageResponse.<ProjectDto.ProjectSummary>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(4)
                .totalElements(12L)
                .totalPages(3)
                .last(false)
                .build();
    }

    public ProjectDto.ProjectDetail getProjectDetail(Long id) {
        return ProjectDto.ProjectDetail.builder()
                .id(id)
                .title("쇼핑몰 백엔드 API 개발")
                .workContent("상품 관리, 주문/결제 REST API 설계 및 개발. 외부 PG사(토스페이먼츠) 연동, " +
                        "주문 상태 관리 및 알림 발송 포함.")
                .requiredSkills("Java 17, Spring Boot 3.x, JPA/Hibernate, MySQL 8.0, Redis")
                .estimatedDuration(3)
                .budget(300)
                .recruitStatus("모집중")
                .deadline(LocalDate.of(2026, 6, 30))
                .workType("원격")
                .employmentType("도급")
                .startDate(LocalDate.of(2026, 7, 10))
                .clientName("박클라이언트")
                .applicantCount(7)
                .build();
    }

    public ProjectDto.PageResponse<ApplicationDto.ApplicantItem> getApplicants(Long id, Pageable pageable) {
        List<ApplicationDto.ApplicantItem> content = List.of(
                ApplicationDto.ApplicantItem.builder()
                        .applicationId(42L).developerId(7L)
                        .developerName("이자바")
                        .profileImage("/images/profile/dev07.png")
                        .experienceLevel("고급").techCategory("백엔드")
                        .bidAmount(350)
                        .proposalSummary("유사 쇼핑몰 프로젝트 3건 완료, 즉시 투입 가능합니다.")
                        .appliedAt(LocalDateTime.of(2026, 5, 12, 9, 0))
                        .build(),

                ApplicationDto.ApplicantItem.builder()
                        .applicationId(43L).developerId(12L)
                        .developerName("최스프링")
                        .profileImage("/images/profile/dev12.png")
                        .experienceLevel("중급").techCategory("풀스택")
                        .bidAmount(280)
                        .proposalSummary("Spring Boot + React 풀스택 개발 경험 보유, 빠른 납기 자신합니다.")
                        .appliedAt(LocalDateTime.of(2026, 5, 13, 14, 30))
                        .build()
        );

        return ProjectDto.PageResponse.<ApplicationDto.ApplicantItem>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(2)
                .totalElements(5L)
                .totalPages(3)
                .last(false)
                .build();
    }

    public List<ProjectDto.ProjectSummary> getMyClientProjects() {
        return List.of(
                ProjectDto.ProjectSummary.builder()
                        .id(1L).title("쇼핑몰 백엔드 API 개발")
                        .techStack("Java, Spring Boot, MySQL")
                        .budget(300).recruitStatus("모집중")
                        .deadline(LocalDate.of(2026, 6, 30))
                        .category("백엔드")
                        .employmentType("도급")
                        .estimatedDuration(3)
                        .applicantCount(7)
                        .build(),

                ProjectDto.ProjectSummary.builder()
                        .id(5L).title("사내 인사 관리 시스템 리뉴얼")
                        .techStack("Vue.js, Node.js, Oracle")
                        .budget(500).recruitStatus("진행중")
                        .deadline(LocalDate.of(2026, 8, 31))
                        .category("풀스택")
                        .employmentType("상주")
                        .estimatedDuration(6)
                        .applicantCount(2)
                        .build()
        );
    }
}