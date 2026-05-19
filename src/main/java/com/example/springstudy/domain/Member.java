package com.example.springstudy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.springstudy.domain.Role;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ======= 기본 계정 정보 (회원가입 생략용) =======
    @Column(unique = true, nullable = false)
    private String loginId;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role; // DEVELOPER, CLIENT

    // ======= 프로필 수정 (기능 4: 이미지 업데이트) =======
    private String profileImage; // 서버 로컬 폴더 저장 경로

    // ======= 프로필 수정 (기능 3: 기본정보 수정) =======
    private String supportFields;     // 지원분야 (체크박스)
    private Boolean isAvailable;      // 활동가능여부
    private Boolean isOnsiteAvailable; // 상주가능여부
    private String regionMain;        // 지역1
    private String regionSub;         // 지역2
    private String businessType;      // 형태1
    private String careerYear;        // 형태2

    @Column(columnDefinition = "TEXT")
    private String introduction;      // 소개글

    // ======= 프로필 수정 (기능 3: 검색태그 x버튼 삭제) =======
    private String searchTags;        // 최대 5개 제한 태그들
}