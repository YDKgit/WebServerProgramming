package com.example.springstudy.controller;

import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.dto.ProjectDto;
import com.example.springstudy.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<?> createProject(
            @RequestBody ProjectDto.ProjectCreateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }
        if (!"CLIENT".equals(role)) {
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        ProjectDto.ProjectCreateResponse response = projectService.createProject(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<?> getProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String participation,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 4 : Math.min(size, 50);
        Pageable pageable = PageRequest.of(safePage, safeSize, projectService.toProjectSort(sort));

        ProjectDto.PageResponse<ProjectDto.ProjectSummary> response =
                projectService.getProjects(
                        keyword,
                        type,
                        employmentType,
                        participation,
                        region,
                        status,
                        pageable
                );

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectDetail(@PathVariable Long id, HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }

        ProjectDto.ProjectDetail response = projectService.getProjectDetail(id, memberId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @GetMapping("/{id}/applicants")
    public ResponseEntity<?> getApplicants(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }
        if (!"CLIENT".equals(role)) {
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 2 : Math.min(size, 20);
        Pageable pageable = PageRequest.of(safePage, safeSize, projectService.toApplicantSort());

        ProjectDto.PageResponse<ApplicationDto.ApplicantItem> response =
                projectService.getApplicants(id, pageable, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @GetMapping("/client/my")
    public ResponseEntity<?> getMyClientProjects(HttpSession session) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }
        if (!"CLIENT".equals(role)) {
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        List<ProjectDto.ProjectSummary> response = projectService.getMyClientProjects(memberId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @RequestBody ProjectDto.ProjectUpdateRequest request,
            HttpSession session
    ) {
        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }
        if (!"CLIENT".equals(role)) {
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "의뢰인만 프로젝트를 수정할 수 있습니다."));
        }

        ProjectDto.ProjectDetail response = projectService.updateProject(id, request, memberId);
        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}
