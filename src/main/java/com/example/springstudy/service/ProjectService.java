package com.example.springstudy.service;

import com.example.springstudy.domain.Application;
import com.example.springstudy.domain.ApplicationStatus;
import com.example.springstudy.domain.Member;
import com.example.springstudy.domain.Project;
import com.example.springstudy.domain.ProjectStatus;
import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.dto.ProjectDto;
import com.example.springstudy.exception.ApiException;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public List<Project> getProject(){
        return projectRepository.findAll();
    }

    @Transactional
    public ProjectDto.ProjectCreateResponse createProject(
            ProjectDto.ProjectCreateRequest request, Long memberId) {

        Member client = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        Project project = new Project();
        project.setClient(client);
        project.setTitle(request.getTitle());
        project.setDeadline(request.getDeadline());
        project.setEmploymentType(normalizeEmploymentType(request.getEmploymentType()));
        project.setBudget(request.getBudget());
        project.setDescription(request.getWorkContent());
        project.setRequiredSkills(request.getRequiredSkills());
        project.setParticipationFields(defaultParticipation(request.getParticipationFields()));
        project.setProjectFields("web");
        project.setEstimatedDays(request.getEstimatedDuration());
        project.setWorkType(request.getWorkType());
        project.setKickoffDate(request.getStartDate() == null ? null : request.getStartDate().toString());
        project.setStatus(ProjectStatus.RECRUITING);

        Project saved = projectRepository.save(project);

        return ProjectDto.ProjectCreateResponse.builder()
                .id(saved.getId())
                .message("프로젝트가 등록되었습니다.")
                .build();
    }

    public ProjectDto.PageResponse<ProjectDto.ProjectSummary> getProjects(
            String keyword, String employmentType, Pageable pageable, Long memberId) {
        return getProjects(
                keyword, null, employmentType, null, null, null, null, pageable, memberId
        );
    }

    public ProjectDto.PageResponse<ProjectDto.ProjectSummary> getProjects(
            String keyword,
            String projectType,
            String employmentType,
            String participation,
            String region,
            String status,
            String sort,
            Pageable pageable,
            Long memberId) {

        Specification<Project> specification = buildSpecification(
                keyword, projectType, employmentType, participation, region, status
        );
        Page<Project> projectPage = findProjectPage(specification, pageable, sort);

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
        Project project = findProject(id);
        return toDetail(project);
    }

    public ProjectDto.PageResponse<ApplicationDto.ApplicantItem> getApplicants(
            Long id, Pageable pageable, Long memberId) {

        Project project = findProject(id);
        if (project.getClient() == null || !project.getClient().getId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "지원자 목록을 조회할 권한이 없습니다.");
        }

        Page<Application> appPage = applicationRepository.findByProjectId(id, pageable);
        List<ApplicationDto.ApplicantItem> content = appPage.getContent().stream()
                .map(app -> ApplicationDto.ApplicantItem.builder()
                        .applicationId(app.getId())
                        .developerId(app.getDeveloper().getId())
                        .developerName(app.getDeveloper().getName())
                        .profileImage(app.getDeveloper().getProfileImage())
                        .experienceLevel(app.getExperienceLevel())
                        .techCategory(app.getTechCategory())
                        .bidAmount(app.getBidAmount())
                        .proposalSummary(summarize(app.getProposalContent()))
                        .appliedAt(app.getAppliedAt())
                        .status(applicationStatusOf(app).name())
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
            Long id, ProjectDto.ProjectUpdateRequest request, Long memberId) {

        Project project = findProject(id);
        if (project.getClient() == null || !project.getClient().getId().equals(memberId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "프로젝트를 수정할 권한이 없습니다.");
        }
        if (applicationRepository.countByProjectId(id) > 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "지원자가 있는 프로젝트는 수정할 수 없습니다.");
        }

        project.setTitle(request.getTitle());
        project.setDeadline(request.getDeadline());
        project.setEmploymentType(normalizeEmploymentType(request.getEmploymentType()));
        project.setBudget(request.getBudget());
        project.setDescription(request.getWorkContent());
        project.setRequiredSkills(request.getRequiredSkills());
        if (request.getParticipationFields() != null) {
            project.setParticipationFields(defaultParticipation(request.getParticipationFields()));
        }
        project.setEstimatedDays(request.getEstimatedDuration());
        project.setWorkType(request.getWorkType());
        project.setKickoffDate(request.getStartDate() == null ? null : request.getStartDate().toString());

        return toDetail(projectRepository.save(project));
    }

    private Specification<Project> buildSpecification(
            String keyword,
            String projectType,
            String employmentType,
            String participation,
            String region,
            String status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("requiredSkills")), pattern)
                ));
            }

            addProjectTypePredicate(predicates, projectType, root, criteriaBuilder);

            if (hasText(employmentType)) {
                predicates.add(criteriaBuilder.equal(
                        root.get("employmentType"),
                        normalizeEmploymentType(employmentType)
                ));
            }

            if (hasText(participation)) {
                List<Predicate> options = Arrays.stream(participation.split("\\|"))
                        .filter(this::hasText)
                        .map(option -> participationOptionPredicate(option, root, criteriaBuilder))
                        .toList();
                if (!options.isEmpty()) {
                    predicates.add(criteriaBuilder.or(options.toArray(Predicate[]::new)));
                }
            }

            if (hasText(region)) {
                String pattern = "%" + region.trim() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("meetingRegion"), pattern),
                        criteriaBuilder.like(root.join("client").get("regionMain"), pattern)
                ));
            }

            if ("CLOSED".equalsIgnoreCase(status)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("status"), ProjectStatus.CLOSED),
                        criteriaBuilder.lessThan(root.get("deadline"), LocalDate.now())
                ));
            } else if ("RECRUITING".equalsIgnoreCase(status)) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("status"), ProjectStatus.RECRUITING),
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(root.get("deadline")),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("deadline"), LocalDate.now())
                        )
                ));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void addProjectTypePredicate(
            List<Predicate> predicates,
            String projectType,
            jakarta.persistence.criteria.Root<Project> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {

        if (!hasText(projectType)) {
            return;
        }

        Predicate appProject = criteriaBuilder.or(
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("projectFields")), "app"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%app%"),
                criteriaBuilder.like(root.get("title"), "%모바일%"),
                criteriaBuilder.like(root.get("description"), "%모바일%")
        );

        if ("app".equalsIgnoreCase(projectType)) {
            predicates.add(appProject);
        } else if ("web".equalsIgnoreCase(projectType)) {
            predicates.add(criteriaBuilder.not(appProject));
        } else {
            predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("projectFields")),
                    projectType.toLowerCase(Locale.ROOT)
            ));
        }
    }

    private Predicate participationOptionPredicate(
            String option,
            jakarta.persistence.criteria.Root<Project> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {

        Predicate[] parts = Arrays.stream(option.split("\\+"))
                .map(String::trim)
                .filter(this::hasText)
                .map(part -> participationPartPredicate(part, root, criteriaBuilder))
                .toArray(Predicate[]::new);
        return criteriaBuilder.and(parts);
    }

    private Predicate participationPartPredicate(
            String part,
            jakarta.persistence.criteria.Root<Project> root,
            jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder) {

        Predicate configured = criteriaBuilder.like(
                root.get("participationFields"),
                "%" + part + "%"
        );
        if (!"개발".equals(part)) {
            return criteriaBuilder.or(
                    configured,
                    criteriaBuilder.like(root.get("projectFields"), "%" + part + "%")
            );
        }

        return criteriaBuilder.or(
                configured,
                root.get("projectFields").in(
                        "백엔드", "프론트엔드", "풀스택", "DevOps", "web", "app"
                )
        );
    }

    private Page<Project> findProjectPage(
            Specification<Project> specification, Pageable pageable, String sort) {

        if ("applicantsDesc".equalsIgnoreCase(sort)) {
            List<Project> projects = projectRepository.findAll(specification);
            projects.sort(Comparator
                    .comparingInt((Project project) ->
                            applicationRepository.countByProjectId(project.getId()))
                    .reversed()
                    .thenComparing(Project::getId, Comparator.reverseOrder()));

            int start = Math.min((int) pageable.getOffset(), projects.size());
            int end = Math.min(start + pageable.getPageSize(), projects.size());
            return new PageImpl<>(
                    projects.subList(start, end),
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    projects.size()
            );
        }

        Sort projectSort = switch (sort == null ? "latest" : sort) {
            case "deadline" -> Sort.by(Sort.Direction.ASC, "deadline");
            case "budgetDesc" -> Sort.by(Sort.Direction.DESC, "budget");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable projectPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                projectSort
        );
        return projectRepository.findAll(specification, projectPageable);
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
                .participationFields(participationOf(project))
                .description(project.getDescription())
                .meetingRegion(meetingRegionOf(project))
                .employmentType(project.getEmploymentType())
                .estimatedDuration(project.getEstimatedDays())
                .applicantCount(applicationRepository.countByProjectId(project.getId()))
                .clientId(project.getClient() == null ? null : project.getClient().getId())
                .build();
    }

    private ProjectDto.ProjectDetail toDetail(Project project) {
        return ProjectDto.ProjectDetail.builder()
                .id(project.getId())
                .title(project.getTitle())
                .workContent(project.getDescription())
                .requiredSkills(project.getRequiredSkills())
                .participationFields(participationOf(project))
                .estimatedDuration(project.getEstimatedDays())
                .budget(project.getBudget())
                .recruitStatus(effectiveStatus(project).name())
                .deadline(project.getDeadline())
                .workType(project.getWorkType())
                .employmentType(project.getEmploymentType())
                .startDate(parseDate(project.getKickoffDate()))
                .clientName(project.getClient() == null ? null : project.getClient().getName())
                .applicantCount(applicationRepository.countByProjectId(project.getId()))
                .clientId(project.getClient() == null ? null : project.getClient().getId())
                .build();
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
    }

    private String summarize(String content) {
        if (content == null) {
            return null;
        }
        return content.substring(0, Math.min(30, content.length()));
    }

    private String participationOf(Project project) {
        if (hasText(project.getParticipationFields())) {
            return project.getParticipationFields();
        }
        String field = project.getProjectFields();
        if (field != null && (field.contains("기획") || field.contains("디자인"))) {
            return field;
        }
        return "개발";
    }

    private String meetingRegionOf(Project project) {
        if (hasText(project.getMeetingRegion())) {
            return project.getMeetingRegion();
        }
        return project.getClient() == null ? null : project.getClient().getRegionMain();
    }

    private ProjectStatus effectiveStatus(Project project) {
        if (project.getStatus() == ProjectStatus.CLOSED
                || (project.getDeadline() != null && project.getDeadline().isBefore(LocalDate.now()))) {
            return ProjectStatus.CLOSED;
        }
        return ProjectStatus.RECRUITING;
    }

    private ApplicationStatus applicationStatusOf(Application application) {
        return application.getStatus() == null ? ApplicationStatus.PENDING : application.getStatus();
    }

    private String normalizeEmploymentType(String employmentType) {
        if ("OUTSOURCING".equalsIgnoreCase(employmentType)) {
            return "도급";
        }
        if ("RESIDENT".equalsIgnoreCase(employmentType)) {
            return "상주";
        }
        return employmentType;
    }

    private String defaultParticipation(String participationFields) {
        return hasText(participationFields) ? participationFields : "개발";
    }

    private LocalDate parseDate(String value) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
