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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public List<Application> getApplication(){
        return applicationRepository.findAll();
    }

    // 이메일(@) 또는 전화번호(숫자 8자리 이상) 포함 여부 검사
    private void validateProposalContent(String content) {
        if (content == null) return;
        if (content.contains("@")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제안 내용에 이메일을 포함할 수 없습니다.");
        }
        if (content.replaceAll("[^0-9]", "").length() >= 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "제안 내용에 전화번호를 포함할 수 없습니다.");
        }
    }

    @Transactional
    public ApplicationDto.ApplyResponse apply(ApplicationDto.ApplyRequest request, Long memberId) {
        if (memberId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (request.getProjectId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "프로젝트 ID가 필요합니다.");
        }

        validateProposalContent(request.getProposalContent());

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

        Application application = new Application();
        application.setProject(project);
        application.setDeveloper(developer);
        application.setWorkDuration(request.getWorkDuration());
        application.setBidAmount(request.getBidAmount());
        application.setProposalContent(request.getProposalContent());
        application.setTechCategory(request.getTechCategory());
        application.setExperienceLevel(request.getExperienceLevel());
        application.setHeadcount(request.getHeadcount());
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

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "지원서를 찾을 수 없습니다."));

        boolean isDeveloperOwner = member.getRole() == Role.DEVELOPER
                && app.getDeveloper().getId().equals(memberId);
        boolean isClientOwner = member.getRole() == Role.CLIENT
                && app.getProject().getClient().getId().equals(memberId);
        if (!isDeveloperOwner && !isClientOwner) {
            throw new ApiException(HttpStatus.FORBIDDEN, "지원서를 조회할 권한이 없습니다.");
        }

        return toDetail(app);
    }

    @Transactional
    public ApplicationDto.ApplicationDetail acceptApplication(Long applicationId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
        if (member.getRole() != Role.CLIENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "의뢰인만 지원자를 수락할 수 있습니다.");
        }

        Application app = applicationRepository.findByIdAndProjectClientId(applicationId, memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "해당 지원서를 수락할 권한이 없습니다."));
        if (statusOf(app) == ApplicationStatus.ACCEPTED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 수락된 지원서입니다.");
        }

        app.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(app);
        return toDetail(app);
    }

    private ApplicationDto.ApplicationDetail toDetail(Application app) {
        return ApplicationDto.ApplicationDetail.builder()
                .applicationId(app.getId())
                .projectId(app.getProject().getId())
                .projectTitle(app.getProject().getTitle())
                .developerName(app.getDeveloper().getName())
                .workDuration(app.getWorkDuration())
                .bidAmount(app.getBidAmount())
                .proposalContent(app.getProposalContent())
                .techCategory(app.getTechCategory())
                .experienceLevel(app.getExperienceLevel())
                .headcount(app.getHeadcount())
                .appliedAt(app.getAppliedAt())
                .status(statusOf(app).name())
                .build();
    }

    private boolean isClosed(Project project) {
        return project.getStatus() == ProjectStatus.CLOSED
                || (project.getDeadline() != null && project.getDeadline().isBefore(LocalDate.now()));
    }

    private ApplicationStatus statusOf(Application application) {
        return application.getStatus() == null ? ApplicationStatus.PENDING : application.getStatus();
    }
}
