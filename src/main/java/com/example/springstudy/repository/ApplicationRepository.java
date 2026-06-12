package com.example.springstudy.repository;


import com.example.springstudy.domain.Application;
import com.example.springstudy.dto.ApplicationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    int countByProjectId(Long projectId);
    boolean existsByProjectIdAndDeveloperId(Long projectId, Long developerId);
    org.springframework.data.domain.Page<Application> findByProjectId(Long projectId, org.springframework.data.domain.Pageable pageable);
    List<Application> findByDeveloperId(Long developerId);
    Optional<Application> findByIdAndProjectClientId(Long applicationId, Long clientId);
}
