package com.example.springstudy.repository;

import com.example.springstudy.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    List<Project> findByClientId(Long clientId);

    // 고용형태(도급/상주) 필터링 + 페이징
    Page<Project> findByEmploymentType(String employmentType, Pageable pageable);

    // 제목에 키워드 포함 + 페이징 (대소문자 무시)
    Page<Project> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 고용형태 + 제목 키워드 동시 필터링 + 페이징
    Page<Project> findByEmploymentTypeAndTitleContainingIgnoreCase(String employmentType, String keyword, Pageable pageable);
}
