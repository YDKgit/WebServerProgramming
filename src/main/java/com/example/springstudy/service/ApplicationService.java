package com.example.springstudy.service;


import com.example.springstudy.domain.Application;
import com.example.springstudy.domain.Member;
import com.example.springstudy.domain.Project;
import com.example.springstudy.dto.ApplicationDto;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new RuntimeException("제안 내용에 이메일을 포함할 수 없습니다.");
        }
        if (content.replaceAll("[^0-9]", "").length() >= 8) {
            throw new RuntimeException("제안 내용에 전화번호를 포함할 수 없습니다.");
        }
    }

    public ApplicationDto.ApplyResponse apply(ApplicationDto.ApplyRequest request, Long memberId) {

        // 이메일/전화번호 필터링 검사
        validateProposalContent(request.getProposalContent());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        Member developer = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Application application = new Application();
        application.setProject(project);
        application.setDeveloper(developer);
        application.setWorkDuration(request.getWorkDuration());
        application.setBidAmount(request.getBidAmount());
        application.setProposalContent(request.getProposalContent());
        application.setTechCategory(request.getTechCategory());
        application.setExperienceLevel(request.getExperienceLevel());
        application.setHeadcount(request.getHeadcount());
        Application saved = applicationRepository.save(application);

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
                        .status("검토중")
                        .appliedAt(app.getAppliedAt())
                        .bidAmount(app.getBidAmount())
                        .build())
                .collect(Collectors.toList());
    }

    public ApplicationDto.ApplicationDetail getApplicationDetail(Long applicationId, Long memberId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("지원서를 찾을 수 없습니다."));


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
                .build();
    }
}