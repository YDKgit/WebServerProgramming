package com.example.springstudy.service;

import com.example.springstudy.domain.Member;
import com.example.springstudy.dto.MemberDto;
import com.example.springstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getMember(){
        return memberRepository.findAll();
    }

    public MemberDto.ProfileResponse getProfile(Long memberId){
       Member member = memberRepository.findById(memberId)
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

    public MemberDto.ProfileResponse updateProfile(MemberDto.ProfileUpdateRequest request, Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을수 없습니다"));

        // 검색태그 5개 초과 시 예외 처리
        if (request.getSearchTags() != null) {
            String[] tags = request.getSearchTags().split(",");
            if (tags.length > 5) {
                throw new RuntimeException("검색 태그는 최대 5개까지만 등록할 수 있습니다.");
            }
        }

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

    public MemberDto.ImageUploadResponse uploadImage(MultipartFile image, Long memberId) {
        try {
            String uploadDir = "src/main/resources/static/images/profile/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = memberId + "_" + image.getOriginalFilename();
            Path savePath = Paths.get(uploadDir + fileName);
            Files.copy(image.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            String imagePath = "/images/profile/" + fileName;

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            member.setProfileImage(imagePath);
            memberRepository.save(member);

            return MemberDto.ImageUploadResponse.builder()
                    .profileImage(imagePath)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.");
        }
    }

}
