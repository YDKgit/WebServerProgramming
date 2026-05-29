package com.example.springstudy.service;

import com.example.springstudy.domain.Project;
import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.ProjectDto;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public List<Project> getProject(){
        return projectRepository.findAll();
    }

    public ProjectDto.ProjectCreateResponse createProject(ProjectDto.ProjectCreateRequest request, Long memberId) {

        com.example.springstudy.domain.Member client = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Project project = new Project();
        project.setClient(client);
        project.setTitle(request.getTitle());
        project.setDeadline(request.getDeadline());
        project.setEmploymentType(request.getEmploymentType());
        project.setBudget(request.getBudget());
        project.setDescription(request.getWorkContent());
        project.setRequiredSkills(request.getRequiredSkills());
        project.setEstimatedDays(request.getEstimatedDuration());
        project.setWorkType(request.getWorkType());

        Project saved = projectRepository.save(project);

        return ProjectDto.ProjectCreateResponse.builder()
                .id(saved.getId())
                .message("프로젝트가 등록되었습니다.")
                .build();
    }

    public ProjectDto.PageResponse<ProjectDto.ProjectSummary> getProjects(String keyword, String employmentType, Pageable pageable, Long memberId) {

        // keyword, employmentType 조합에 따라 다른 쿼리 실행
        Page<Project> projectPage;
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasType = employmentType != null && !employmentType.isBlank();

        if (hasKeyword && hasType) {
            // 키워드 + 고용형태 둘 다 있을 때
            projectPage = projectRepository.findByEmploymentTypeAndTitleContainingIgnoreCase(employmentType, keyword, pageable);
        } else if (hasKeyword) {
            // 키워드만 있을 때
            projectPage = projectRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else if (hasType) {
            // 고용형태만 있을 때
            projectPage = projectRepository.findByEmploymentType(employmentType, pageable);
        } else {
            // 필터 없을 때 전체 조회
            projectPage = projectRepository.findAll(pageable);
        }

        List<ProjectDto.ProjectSummary> content = projectPage.getContent().stream()
                .map(project -> ProjectDto.ProjectSummary.builder()
                        .id(project.getId())
                        .title(project.getTitle())
                        .techStack(project.getRequiredSkills())
                        .budget(project.getBudget())
                        .recruitStatus(project.getStatus() != null ? project.getStatus().name() : null)
                        .deadline(project.getDeadline())
                        .category(project.getProjectFields())
                        .employmentType(project.getEmploymentType())
                        .estimatedDuration(project.getEstimatedDays())
                        .applicantCount(applicationRepository.countByProjectId(project.getId()))
                        .build())
                .collect(Collectors.toList());

        return ProjectDto.PageResponse.<ProjectDto.ProjectSummary>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .last(projectPage.isLast())
                .build();
    }

    public ProjectDto.ProjectDetail getProjectDetail(Long id,Long memberId) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        return ProjectDto.ProjectDetail.builder()
                .id(project.getId())
                .title(project.getTitle())
                .workContent(project.getDescription())
                .requiredSkills(project.getRequiredSkills())
                .estimatedDuration(project.getEstimatedDays())
                .budget(project.getBudget())
                .recruitStatus(project.getStatus() != null ? project.getStatus().name() : null)
                .deadline(project.getDeadline())
                .workType(project.getWorkType())
                .employmentType(project.getEmploymentType())
                .clientName(project.getClient() != null ? project.getClient().getName() : null)
                .applicantCount(applicationRepository.countByProjectId(project.getId()))
                .build();
    }

    public ProjectDto.PageResponse<ApplicationDto.ApplicantItem> getApplicants(Long id, Pageable pageable, Long memberId) {

        Page<com.example.springstudy.domain.Application> appPage = applicationRepository.findByProjectId(id, pageable);

        List<ApplicationDto.ApplicantItem> content = appPage.getContent().stream()
                .map(app -> ApplicationDto.ApplicantItem.builder()
                        .applicationId(app.getId())
                        .developerId(app.getDeveloper().getId())
                        .developerName(app.getDeveloper().getName())
                        .profileImage(app.getDeveloper().getProfileImage())
                        .experienceLevel(app.getExperienceLevel())
                        .techCategory(app.getTechCategory())
                        .bidAmount(app.getBidAmount())
                        .proposalSummary(app.getProposalContent() != null ?
                                app.getProposalContent().substring(0, Math.min(30, app.getProposalContent().length())) : null)
                        .appliedAt(app.getAppliedAt())
                        .build())
                .collect(Collectors.toList());

        return ProjectDto.PageResponse.<ApplicationDto.ApplicantItem>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(appPage.getTotalElements())
                .totalPages(appPage.getTotalPages())
                .last(appPage.isLast())
                .build();
    }

    public List<ProjectDto.ProjectSummary> getMyClientProjects(Long memberId) {

        List<Project> projects = projectRepository.findByClientId(memberId);

        return projects.stream()
                .map(project -> ProjectDto.ProjectSummary.builder()
                        .id(project.getId())
                        .title(project.getTitle())
                        .techStack(project.getRequiredSkills())
                        .budget(project.getBudget())
                        .recruitStatus(project.getStatus() != null ? project.getStatus().name() : null)
                        .deadline(project.getDeadline())
                        .category(project.getProjectFields())
                        .employmentType(project.getEmploymentType())
                        .estimatedDuration(project.getEstimatedDays())
                        .applicantCount(applicationRepository.countByProjectId(project.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}