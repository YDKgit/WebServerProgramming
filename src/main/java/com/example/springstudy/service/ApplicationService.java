package com.example.springstudy.service;

import com.example.springstudy.domain.Application;
import com.example.springstudy.domain.ApplicationStatus;
import com.example.springstudy.domain.Member;
import com.example.springstudy.domain.Project;
import com.example.springstudy.domain.ProjectStatus;
import com.example.springstudy.domain.Role;
import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.exception.ApiException;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern MOBILE_PHONE_PATTERN = Pattern.compile("^01[016789]\\d{7,8}$");

    public List<Application> getApplication(){
        return applicationRepository.findAll();
    }

    @Transactional
    public ApplicationDto.ApplyResponse apply(ApplicationDto.ApplyRequest request, Long memberId) {
        if (memberId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (request.getProjectId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "프로젝트 ID가 필요합니다.");
        }

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));
        Member developer = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        if (developer.getRole() != Role.DEVELOPER) {
            throw new ApiException(HttpStatus.FORBIDDEN, "개발자만 프로젝트에 지원할 수 있습니다.");
        }
        if (applicationRepository.existsByProjectIdAndDeveloperId(project.getId(), developer.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 지원한 프로젝트입니다.");
        }
        if (isClosed(project)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "마감된 프로젝트에는 지원할 수 없습니다.");
        }

        validateCommonFields(request);
        validateFieldsByProjectType(project, request);
        boolean residentProject = isResidentProject(project);

        Application application = new Application();
        application.setProject(project);
        application.setDeveloper(developer);
        application.setWorkDuration(residentProject ? null : request.getWorkDuration());
        application.setBidAmount(request.getBidAmount());
        application.setProposalContent(request.getProposalContent());
        application.setEmail(request.getEmail().trim());
        application.setPhone(normalizePhone(request.getPhone()));
        application.setTechCategory(residentProject ? request.getTechCategory() : null);
        application.setExperienceLevel(residentProject ? request.getExperienceLevel() : null);
        application.setHeadcount(residentProject ? normalizedHeadcount(request.getHeadcount()) : null);
        application.setStatus(ApplicationStatus.PENDING);

        Application saved;
        try {
            saved = applicationRepository.saveAndFlush(application);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 지원한 프로젝트입니다.");
        }

        return ApplicationDto.ApplyResponse.builder()
                .message("지원이 완료되었습니다.")
                .applicationId(saved.getId())
                .build();
    }

    public List<ApplicationDto.MyApplicationItem> getMyApplications(Long memberId) {
        List<Application> applications = applicationRepository.findByDeveloperId(memberId);

        return applications.stream()
                .map(app -> ApplicationDto.MyApplicationItem.builder()
                        .applicationId(app.getId())
                        .projectId(app.getProject().getId())
                        .projectTitle(app.getProject().getTitle())
                        .status(statusOf(app).name())
                        .appliedAt(app.getAppliedAt())
                        .bidAmount(app.getBidAmount())
                        .build())
                .collect(Collectors.toList());
    }

    public ApplicationDto.ApplicationDetail getApplicationDetail(Long applicationId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "지원서를 찾을 수 없습니다."));

        boolean developerOwner = member.getRole() == Role.DEVELOPER
                && application.getDeveloper().getId().equals(memberId);
        boolean clientOwner = member.getRole() == Role.CLIENT
                && application.getProject().getClient().getId().equals(memberId);
        if (!developerOwner && !clientOwner) {
            throw new ApiException(HttpStatus.FORBIDDEN, "지원서를 조회할 권한이 없습니다.");
        }

        return toDetail(application);
    }

    @Transactional
    public ApplicationDto.ApplicationDetail acceptApplication(Long applicationId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        if (member.getRole() != Role.CLIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "의뢰인만 지원자를 수락할 수 있습니다.");
        }

        Application application = applicationRepository.findByIdAndProjectClientId(applicationId, memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "해당 지원서를 수락할 권한이 없습니다."));
        if (statusOf(application) == ApplicationStatus.ACCEPTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 수락된 지원서입니다.");
        }

        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);
        return toDetail(application);
    }

    private ApplicationDto.ApplicationDetail toDetail(Application application) {
        return ApplicationDto.ApplicationDetail.builder()
                .applicationId(application.getId())
                .projectId(application.getProject().getId())
                .projectTitle(application.getProject().getTitle())
                .developerName(application.getDeveloper().getName())
                .email(application.getEmail())
                .phone(application.getPhone())
                .workDuration(application.getWorkDuration())
                .bidAmount(application.getBidAmount())
                .proposalContent(application.getProposalContent())
                .techCategory(application.getTechCategory())
                .experienceLevel(application.getExperienceLevel())
                .headcount(application.getHeadcount())
                .appliedAt(application.getAppliedAt())
                .status(statusOf(application).name())
                .build();
    }

    private boolean isClosed(Project project) {
        return project.getStatus() == ProjectStatus.CLOSED
                || (project.getDeadline() != null && project.getDeadline().isBefore(LocalDate.now()));
    }

    private ApplicationStatus statusOf(Application application) {
        return application.getStatus() == null ? ApplicationStatus.PENDING : application.getStatus();
    }

    private void validateCommonFields(ApplicationDto.ApplyRequest request) {
        if (!hasText(request.getProposalContent())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "지원 내용을 입력해 주세요.");
        }
        if (!hasText(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이메일을 입력해 주세요.");
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 이메일 형식으로 입력해 주세요.");
        }
        if (!hasText(request.getPhone())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "전화번호를 입력해 주세요.");
        }
        if (!MOBILE_PHONE_PATTERN.matcher(onlyDigits(request.getPhone())).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바른 전화번호 형식으로 입력해 주세요.");
        }
    }

    private void validateFieldsByProjectType(Project project, ApplicationDto.ApplyRequest request) {
        if (isResidentProject(project)) {
            if (request.getBidAmount() == null || request.getBidAmount() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "희망 급여를 입력해 주세요.");
            }
            if (!hasText(request.getTechCategory())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "기술 구분을 입력해 주세요.");
            }
            if (!hasText(request.getExperienceLevel())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "경력 구분을 입력해 주세요.");
            }
            return;
        }

        if (request.getWorkDuration() == null || request.getWorkDuration() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "예상 작업 기간을 입력해 주세요.");
        }
        if (request.getBidAmount() == null || request.getBidAmount() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제안 금액을 입력해 주세요.");
        }
    }

    private boolean isResidentProject(Project project) {
        String type = project.getEmploymentType();
        if (!hasText(type)) {
            return false;
        }
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        return "RESIDENT".equals(normalized) || type.contains("상주");
    }

    private int normalizedHeadcount(Integer headcount) {
        return headcount == null || headcount < 1 ? 1 : headcount;
    }

    private String normalizePhone(String phone) {
        String digits = onlyDigits(phone);
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        if (digits.length() == 10) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }
        return phone.trim();
    }

    private String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
