package com.example.springstudy.service;

import com.example.springstudy.domain.Project;
import com.example.springstudy.domain.ProjectStatus;
import com.example.springstudy.domain.Role;
import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.ProjectDto;
import com.example.springstudy.exception.ApiException;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public Sort toProjectSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        return switch (sort) {
            case "deadline" -> Sort.by(Sort.Direction.ASC, "deadline");
            case "budgetDesc" -> Sort.by(Sort.Direction.DESC, "budget");
            case "applicantsDesc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    public Sort toApplicantSort() {
        return Sort.by(Sort.Direction.DESC, "appliedAt");
    }

    public ProjectDto.ProjectCreateResponse createProject(ProjectDto.ProjectCreateRequest request, Long memberId) {
        com.example.springstudy.domain.Member client = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        if (client.getRole() != Role.CLIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "의뢰인만 프로젝트를 등록할 수 있습니다.");
        }

        Project project = new Project();
        project.setClient(client);
        project.setTitle(request.getTitle());
        project.setDeadline(request.getDeadline());
        project.setEmploymentType(normalizeEmploymentType(request.getEmploymentType()));
        project.setBudget(request.getBudget());
        project.setDescription(request.getWorkContent());
        project.setRequiredSkills(request.getRequiredSkills());
        project.setEstimatedDays(request.getEstimatedDuration());
        project.setWorkType(request.getWorkType());
        project.setKickoffDate(request.getStartDate() != null ? request.getStartDate().toString() : null);
        project.setProjectFields("web");
        project.setParticipationFields(normalizeParticipationForSave(request.getParticipationFields()));
        project.setStatus(ProjectStatus.RECRUITING);

        Project saved = projectRepository.save(project);

        return ProjectDto.ProjectCreateResponse.builder()
                .id(saved.getId())
                .message("프로젝트가 등록되었습니다.")
                .build();
    }

    public ProjectDto.PageResponse<ProjectDto.ProjectSummary> getProjects(
            String keyword,
            String type,
            String employmentType,
            String participation,
            String region,
            String status,
            Pageable pageable
    ) {
        Specification<Project> specification = Specification.where(null);
        if (keyword != null && !keyword.isBlank()) {
            String likeKeyword = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likeKeyword));
        }
        String normalizedType = normalizeProjectType(type);
        if (normalizedType != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("projectFields"), normalizedType));
        }
        String normalizedEmploymentType = normalizeEmploymentTypeFilter(employmentType);
        if (normalizedEmploymentType != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("employmentType"), normalizedEmploymentType));
        }
        specification = specification.and(participationSpecification(participation));
        if (region != null && !region.isBlank() && !"ALL".equalsIgnoreCase(region)) {
            String regionKeyword = "%" + region.trim().toLowerCase(Locale.ROOT) + "%";
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("meetingRegion")), regionKeyword));
        }
        specification = specification.and(statusSpecification(status));

        Page<Project> projectPage = projectRepository.findAll(specification, pageable);

        List<ProjectDto.ProjectSummary> content = projectPage.getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ProjectDto.PageResponse.<ProjectDto.ProjectSummary>builder()
                .content(content)
                .pageNumber(projectPage.getNumber())
                .pageSize(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .last(projectPage.isLast())
                .build();
    }

    public ProjectDto.ProjectDetail getProjectDetail(Long id, Long memberId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        com.example.springstudy.domain.Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        if (member.getRole() == Role.CLIENT && !project.getClient().getId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인이 작성한 프로젝트만 조회할 수 있습니다.");
        }

        return ProjectDto.ProjectDetail.builder()
                .id(project.getId())
                .title(project.getTitle())
                .workContent(project.getDescription())
                .requiredSkills(project.getRequiredSkills())
                .participationFields(project.getParticipationFields())
                .estimatedDuration(project.getEstimatedDays())
                .budget(project.getBudget())
                .recruitStatus(effectiveStatus(project).name())
                .deadline(project.getDeadline())
                .workType(project.getWorkType())
                .employmentType(project.getEmploymentType())
                .startDate(parseDate(project.getKickoffDate()))
                .clientName(project.getClient() != null ? project.getClient().getName() : null)
                .applicantCount(applicationRepository.countByProjectId(project.getId()))
                .clientId(project.getClient() != null ? project.getClient().getId() : null)
                .build();
    }

    public ProjectDto.PageResponse<ApplicationDto.ApplicantItem> getApplicants(Long id, Pageable pageable, Long memberId) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        if (project.getClient() == null || !project.getClient().getId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인 프로젝트의 지원자만 조회할 수 있습니다.");
        }
        Page<com.example.springstudy.domain.Application> appPage = applicationRepository.findByProjectId(id, pageable);

        List<ApplicationDto.ApplicantItem> content = appPage.getContent().stream()
                .map(app -> ApplicationDto.ApplicantItem.builder()
                        .applicationId(app.getId())
                        .developerId(app.getDeveloper() != null ? app.getDeveloper().getId() : null)
                        .developerName(app.getDeveloper() != null ? app.getDeveloper().getName() : null)
                        .profileImage(app.getDeveloper() != null ? app.getDeveloper().getProfileImage() : null)
                        .experienceLevel(app.getExperienceLevel())
                        .techCategory(app.getTechCategory())
                        .bidAmount(app.getBidAmount())
                        .proposalSummary(summary(app.getProposalContent()))
                        .appliedAt(app.getAppliedAt())
                        .status(app.getStatus() != null ? app.getStatus().name() : "PENDING")
                        .build())
                .collect(Collectors.toList());

        return ProjectDto.PageResponse.<ApplicationDto.ApplicantItem>builder()
                .content(content)
                .pageNumber(appPage.getNumber())
                .pageSize(appPage.getSize())
                .totalElements(appPage.getTotalElements())
                .totalPages(appPage.getTotalPages())
                .last(appPage.isLast())
                .build();
    }

    public List<ProjectDto.ProjectSummary> getMyClientProjects(Long memberId) {
        return projectRepository.findByClientId(memberId).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto.ProjectDetail updateProject(
            Long projectId,
            ProjectDto.ProjectUpdateRequest request,
            Long memberId
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        if (project.getClient() == null || !project.getClient().getId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "본인이 작성한 프로젝트만 수정할 수 있습니다.");
        }
        if (applicationRepository.countByProjectId(projectId) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "지원자가 있는 프로젝트는 수정할 수 없습니다.");
        }

        project.setTitle(request.getTitle());
        project.setDeadline(request.getDeadline());
        project.setEmploymentType(normalizeEmploymentType(request.getEmploymentType()));
        project.setBudget(request.getBudget());
        project.setDescription(request.getWorkContent());
        project.setRequiredSkills(request.getRequiredSkills());
        if (request.getParticipationFields() != null && !request.getParticipationFields().isBlank()) {
            project.setParticipationFields(normalizeParticipationForSave(request.getParticipationFields()));
        }
        project.setEstimatedDays(request.getEstimatedDuration());
        project.setWorkType(request.getWorkType());
        project.setKickoffDate(request.getStartDate() != null ? request.getStartDate().toString() : null);
        project.setStatus(request.getDeadline() != null && request.getDeadline().isBefore(LocalDate.now())
                ? ProjectStatus.CLOSED
                : ProjectStatus.RECRUITING);
        projectRepository.save(project);

        return getProjectDetail(projectId, memberId);
    }

    private ProjectDto.ProjectSummary toSummary(Project project) {
        return ProjectDto.ProjectSummary.builder()
                .id(project.getId())
                .title(project.getTitle())
                .techStack(project.getRequiredSkills())
                .budget(project.getBudget())
                .recruitStatus(effectiveStatus(project).name())
                .deadline(project.getDeadline())
                .category(project.getProjectFields())
                .participationFields(project.getParticipationFields())
                .description(project.getDescription())
                .meetingRegion(project.getMeetingRegion())
                .employmentType(project.getEmploymentType())
                .estimatedDuration(project.getEstimatedDays())
                .applicantCount(applicationRepository.countByProjectId(project.getId()))
                .clientId(project.getClient() != null ? project.getClient().getId() : null)
                .build();
    }

    private Specification<Project> statusSpecification(String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return null;
        }
        LocalDate today = LocalDate.now();
        if ("RECRUITING".equalsIgnoreCase(status)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("status"), ProjectStatus.RECRUITING),
                    criteriaBuilder.or(
                            criteriaBuilder.isNull(root.get("deadline")),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("deadline"), today)
                    )
            );
        }
        if ("CLOSED".equalsIgnoreCase(status)) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("status"), ProjectStatus.CLOSED),
                    criteriaBuilder.lessThan(root.get("deadline"), today)
            );
        }
        return null;
    }

    private String normalizeProjectType(String type) {
        if (type == null || type.isBlank() || "ALL".equalsIgnoreCase(type)) {
            return null;
        }
        String normalized = type.toLowerCase(Locale.ROOT);
        if ("web".equals(normalized) || "app".equals(normalized)) {
            return normalized;
        }
        return null;
    }

    private String normalizeEmploymentType(String employmentType) {
        if ("RESIDENT".equals(employmentType) || "상주".equals(employmentType)) {
            return "상주";
        }
        return "도급";
    }

    private String normalizeEmploymentTypeFilter(String employmentType) {
        if (employmentType == null
                || employmentType.isBlank()
                || "ALL".equalsIgnoreCase(employmentType)) {
            return null;
        }
        return normalizeEmploymentType(employmentType);
    }

    private Specification<Project> participationSpecification(String participation) {
        if (participation == null || participation.isBlank()) {
            return null;
        }
        List<String> values = java.util.Arrays.stream(participation.split("\\|"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.replace("+", ","))
                .distinct()
                .toList();
        if (values.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                values.stream()
                        .map(value -> criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("participationFields")),
                                value.toLowerCase(Locale.ROOT)
                        ))
                        .toArray(jakarta.persistence.criteria.Predicate[]::new)
        );
    }

    private String normalizeParticipationForSave(String participationFields) {
        return participationFields == null || participationFields.isBlank()
                ? "개발"
                : participationFields.trim();
    }

    private String summary(String content) {
        if (content == null) {
            return null;
        }
        return content.substring(0, Math.min(30, content.length()));
    }

    private ProjectStatus effectiveStatus(Project project) {
        if (project.getStatus() == ProjectStatus.CLOSED
                || (project.getDeadline() != null && project.getDeadline().isBefore(LocalDate.now()))) {
            return ProjectStatus.CLOSED;
        }
        return ProjectStatus.RECRUITING;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }
}
