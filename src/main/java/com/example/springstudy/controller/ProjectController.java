package com.example.springstudy.controller;

import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.CommonResponse;
import com.example.springstudy.dto.ProjectDto;
import com.example.springstudy.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 등록 (의뢰인)")
    @PostMapping
    public ResponseEntity<?> createProject(
            @RequestBody ProjectDto.ProjectCreateRequest request, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        
        if(role == null || !role.equals("CLIENT")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        ProjectDto.ProjectCreateResponse response = projectService.createProject(request, memberId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.ok(response));
    }

    @Operation(summary = "프로젝트 검색 목록 조회", description = "페이지당 4개씩 반환합니다.")
    @GetMapping
    public ResponseEntity<?> getProjects(
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @Parameter(description = "고용형태 필터 (도급 또는 상주)") @RequestParam(required = false) String employmentType,
            @PageableDefault(size = 4, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }

        ProjectDto.PageResponse<ProjectDto.ProjectSummary> response = projectService.getProjects(keyword, employmentType, pageable, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "프로젝트 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectDetail(
            @PathVariable Long id, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        if (memberId == null) {
            return ResponseEntity.status(401).body(new CommonResponse<>(false, "로그인이 필요합니다."));
        }

        ProjectDto.ProjectDetail response = projectService.getProjectDetail(id, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "프로젝트 지원자 목록 조회 (의뢰인용)", description = "페이지당 2개씩 반환합니다.")
    @GetMapping("/{id}/applicants")
    public ResponseEntity<?> getApplicants(
            @PathVariable Long id,
            @PageableDefault(size = 2, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable, HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if(role == null || !role.equals("CLIENT")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        ProjectDto.PageResponse<ApplicationDto.ApplicantItem> response = projectService.getApplicants(id, pageable, memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }

    @Operation(summary = "내가 의뢰한 프로젝트 목록 (의뢰인 마이페이지)")
    @GetMapping("/client/my")
    public ResponseEntity<?> getMyClientProjects(HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");
        String role = (String) session.getAttribute("loginMemberRole");

        if(role == null || !role.equals("CLIENT")){
            return ResponseEntity.status(403).body(new CommonResponse<>(false, "권한이 없습니다."));
        }

        List<ProjectDto.ProjectSummary> response = projectService.getMyClientProjects(memberId);

        return ResponseEntity.ok(CommonResponse.ok(response));
    }
}