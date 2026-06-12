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

    Page<Project> findByProjectFields(String projectFields, Pageable pageable);

    Page<Project> findByProjectFieldsAndTitleContainingIgnoreCase(String projectFields, String keyword, Pageable pageable);

    Page<Project> findByEmploymentType(String employmentType, Pageable pageable);

    Page<Project> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Project> findByEmploymentTypeAndTitleContainingIgnoreCase(String employmentType, String keyword, Pageable pageable);
}
