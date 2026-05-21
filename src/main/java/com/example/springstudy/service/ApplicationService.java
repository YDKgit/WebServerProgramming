package com.example.springstudy.service;


import com.example.springstudy.dto.ApplicationDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    public ApplicationDto.ApplyResponse apply(ApplicationDto.ApplyRequest request) {

        return ApplicationDto.ApplyResponse.builder()
                .message("지원이 완료되었습니다.")
                .applicationId(42L)
                .build();
    }

    public List<ApplicationDto.MyApplicationItem> getMyApplications() {

        return  List.of(
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
    }

    public ApplicationDto.ApplicationDetail getApplicationDetail(Long applicationId) {

        return ApplicationDto.ApplicationDetail.builder()
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
    }
}