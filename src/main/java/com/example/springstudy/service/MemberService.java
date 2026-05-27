package com.example.springstudy.service;

import com.example.springstudy.domain.Member;
import com.example.springstudy.dto.MemberDto;
import com.example.springstudy.repository.ApplicationRepository;
import com.example.springstudy.repository.MemberRepository;
import com.example.springstudy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getMember(){
        return memberRepository.findAll();
    }

    public MemberDto.ProfileResponse getProfile(){
       Member member = memberRepository.findById(1L)
               .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        return MemberDto.ProfileResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .supportFields(member.getSupportFields())
                .searchTags(member.getSearchTags())
                .introduction(member.getIntroduction())
                .isAvailable(member.getIsAvailable())
                .isOnsiteAvailable(member.getIsOnsiteAvailable())
                .regionMain(member.getRegionMain())
                .regionSub(member.getRegionSub())
                .businessType(member.getBusinessType())
                .careerYear(member.getCareerYear())
                .build();
    }

    public MemberDto.ProfileResponse updateProfile(MemberDto.ProfileUpdateRequest request){

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("회원을 찾을수 없습니다"));

        member.setSupportFields(request.getSupportFields());
        member.setSearchTags(request.getSearchTags());
        member.setIntroduction(request.getIntroduction());
        member.setIsAvailable(request.getIsAvailable());
        member.setIsOnsiteAvailable(request.getIsOnsiteAvailable());
        member.setRegionMain(request.getRegionMain());
        member.setRegionSub(request.getRegionSub());
        member.setBusinessType(request.getBusinessType());
        member.setCareerYear(request.getCareerYear());

        memberRepository.save(member);

        return MemberDto.ProfileResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .supportFields(member.getSupportFields())
                .searchTags(member.getSearchTags())
                .introduction(member.getIntroduction())
                .isAvailable(member.getIsAvailable())
                .isOnsiteAvailable(member.getIsOnsiteAvailable())
                .regionMain(member.getRegionMain())
                .regionSub(member.getRegionSub())
                .businessType(member.getBusinessType())
                .careerYear(member.getCareerYear())
                .build();
    }

    public MemberDto.ImageUploadResponse uploadImage() {
        return MemberDto.ImageUploadResponse.builder()
                .profileImage("/images/profile/dev01_new.png")
                .build();
    }

}
