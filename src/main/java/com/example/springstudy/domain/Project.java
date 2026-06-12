package com.example.springstudy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Member client; // 프로젝트 등록자(의뢰인)

    // ======= 프로젝트 검색 (기능 1: 형태 및 정렬) =======
    private String title;             // 프로젝트명
    private String workType;          // 프로젝트 형태 (도급/상주)
    private String projectFields;     // 참여파트 분류 (개발/디자인 등)
    private String participationFields; // 기획/디자인/개발 조합
    private Integer budget;           // 예산/월급여
    private LocalDateTime createdAt = LocalDateTime.now(); // 정렬 기준용

    // ======= 프로젝트 검색 (기능 2: 페이징 처리) =======
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;     // 모집중/마감 상태

    // ======= 프로젝트 상세 보기 (프로젝트 정보) =======
    private LocalDate deadline;       // 마감일
    private String planningStatus;    // 기획상태
    private String meetingRegion;     // 미팅지역
    @Column(columnDefinition = "TEXT")
    private String description;       // 업무내용
    private String progressMethod;    // 진행방식
    private String requiredSkills;    // 필요기술
    private Integer estimatedDays;    // 예상기간
    private String kickoffDate;       // 킥오프시점
    private String employmentType; // 도급/상주
}
