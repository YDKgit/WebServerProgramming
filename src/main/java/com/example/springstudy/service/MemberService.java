package com.example.springstudy.service;

import com.example.springstudy.domain.Member;
import com.example.springstudy.domain.Role;
import com.example.springstudy.dto.MemberDto;
import com.example.springstudy.exception.ApiException;
import com.example.springstudy.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public List<Member> getMember(){
        return memberRepository.findAll();
    }

    public MemberDto.ProfileResponse getProfile(Long memberId){
        return toProfileResponse(findMember(memberId));
    }

    @Transactional
    public MemberDto.ProfileResponse updateProfile(MemberDto.ProfileUpdateRequest request, Long memberId){
        Member member = findMember(memberId);
        if (request.getName() != null && !request.getName().isBlank()) {
            member.setName(request.getName().trim());
        }
        member.setIntroduction(request.getIntroduction());
        member.setRegionMain(request.getRegionMain());
        member.setRegionSub(request.getRegionSub());
        member.setBusinessType(request.getBusinessType());

        if (member.getRole() == Role.DEVELOPER) {
            List<String> tags = normalizeTags(request.getSearchTags());
            member.setSupportFields(request.getSupportFields());
            member.setSearchTags(String.join(",", tags));
            member.setIsAvailable(request.getIsAvailable());
            member.setIsOnsiteAvailable(request.getIsOnsiteAvailable());
            member.setCareerYear(request.getCareerYear());
        }

        memberRepository.save(member);
        return toProfileResponse(member);
    }

    @Transactional
    public MemberDto.ImageUploadResponse uploadImage(MultipartFile image, Long memberId) {
        if (image == null || image.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "업로드할 이미지 파일을 선택해 주세요.");
        }

        String extension = getImageExtension(image.getContentType());
        Member member = findMember(memberId);
        Path profileUploadDir = Paths.get(uploadDir, "profile").toAbsolutePath().normalize();
        String fileName = UUID.randomUUID() + "." + extension;
        Path savePath = profileUploadDir.resolve(fileName).normalize();

        if (!savePath.startsWith(profileUploadDir)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "올바르지 않은 파일 경로입니다.");
        }

        try {
            Files.createDirectories(profileUploadDir);
            Files.copy(image.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

            String previousImagePath = member.getProfileImage();
            String imagePath = "/uploads/profile/" + fileName;
            member.setProfileImage(imagePath);
            memberRepository.save(member);
            deletePreviousUploadedImage(previousImagePath, savePath);

            return MemberDto.ImageUploadResponse.builder()
                    .profileImage(imagePath)
                    .build();
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 중 오류가 발생했습니다.");
        }
    }

    private Member findMember(Long memberId) {
        if (memberId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    private List<String> normalizeTags(String searchTags) {
        if (searchTags == null || searchTags.isBlank()) {
            return List.of();
        }

        List<String> tags = Arrays.stream(searchTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .toList();

        if (tags.size() > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "검색태그는 최대 5개까지 등록할 수 있습니다.");
        }
        return tags;
    }

    private String getImageExtension(String contentType) {
        if (contentType == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            default -> throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "JPG, PNG, GIF, WEBP 이미지 파일만 업로드할 수 있습니다."
            );
        };
    }

    private void deletePreviousUploadedImage(String previousImagePath, Path newImagePath) {
        if (previousImagePath == null || !previousImagePath.startsWith("/uploads/profile/")) {
            return;
        }

        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path previousPath = uploadRoot
                .resolve(previousImagePath.substring("/uploads/".length()))
                .normalize();

        if (!previousPath.equals(newImagePath) && previousPath.startsWith(uploadRoot)) {
            try {
                Files.deleteIfExists(previousPath);
            } catch (IOException ignored) {
                // The new profile image is already stored and remains valid.
            }
        }
    }

    private MemberDto.ProfileResponse toProfileResponse(Member member) {
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
}
