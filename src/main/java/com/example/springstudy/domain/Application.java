package com.example.springstudy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Application {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private Member developer;

    // ======= 프로젝트 지원 (지원서 작성 및 제출) =======
    private Integer workDuration;     // 작업기간
    private Integer bidAmount;        // 지원금액/임금

    @Column(columnDefinition = "TEXT")
    private String proposalContent;   // 지원내용 (연락처 이메일 필터링)

    // ======= 프로젝트 지원 (상주 전용 정보) =======
    private String techCategory;      // 기술구분
    private String experienceLevel;   // 연차구분
    private Integer headcount;        // 인원수

    // ======= 지원한 프로젝트 보기 (기능 5) =======
    private LocalDateTime appliedAt = LocalDateTime.now(); // 지원일자

    // ======= 의뢰한 프로젝트 관리 (지원자 리스트) =======
    // 이 엔티티 자체가 의뢰인 마이페이지에서 '더보기'로 보여줄 지원자 정보가 됩니다.
}