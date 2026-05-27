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

    public ApplicationDto.ApplyResponse apply(ApplicationDto.ApplyRequest request) {

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        Member developer = memberRepository.findById(1L)
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

    public List<ApplicationDto.MyApplicationItem> getMyApplications() {

        List<Application> applications = applicationRepository.findByDeveloperId(1L);

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

    public ApplicationDto.ApplicationDetail getApplicationDetail(Long applicationId) {

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