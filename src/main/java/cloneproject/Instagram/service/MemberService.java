package cloneproject.Instagram.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.dto.member.*;
import cloneproject.Instagram.entity.member.Gender;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.UseridAlreadyExistException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.util.AuthUtil;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public MenuMemberDTO getMenuMemberProfile(){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        final Member member = memberRepository.findById(Long.valueOf(memberId))
                                .orElseThrow(MemberDoesNotExistException::new);
        
        return MenuMemberDTO.builder()
                            .memberId(member.getId())
                            .memberUsername(member.getUsername())
                            .memberName(member.getName())
                            .memberImageUrl(member.getImage().getImageUrl())
                            .build();
        
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username){
        final Long memberId = AuthUtil.getLoginedMemberIdOrNull();
        
        final Member member = memberRepository.findByUsername(username)
                                .orElseThrow(MemberDoesNotExistException::new);

        
        UserProfileResponse result = memberRepository.getUserProfile(memberId, member.getUsername());
        
        return result;
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfile(String username){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        final Member member = memberRepository.findByUsername(username)
                                .orElseThrow(MemberDoesNotExistException::new);
        
        MiniProfileResponse result = memberRepository.getMiniProfile(Long.valueOf(memberId), member.getUsername());

        return result;
    }

    @Transactional
    public void uploadMemberImage(MultipartFile uploadedImage){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(memberId))
                                    .orElseThrow(MemberDoesNotExistException::new);

        // 기존 사진 삭제
        Image originalImage = member.getImage();
        s3Uploader.deleteImage("member", originalImage);

        Image image = s3Uploader.uploadImage(uploadedImage, "member");
        member.uploadImage(image);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMemberImage(){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(memberId))
                                    .orElseThrow(MemberDoesNotExistException::new);
        Image image = member.getImage();
        s3Uploader.deleteImage("member", image);
        member.deleteImage();
        memberRepository.save(member);
    }

    public EditProfileResponse getEditProfile(){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(memberId))
                                .orElseThrow(MemberDoesNotExistException::new);
        return EditProfileResponse.builder()
                                .memberUsername(member.getUsername())
                                .memberName(member.getName())
                                .memberImageUrl(member.getImage().getImageUrl())
                                .memberGender(member.getGender().toString())
                                .memberIntroduce(member.getIntroduce())
                                .memberWebsite(member.getWebsite())
                                .memberPhone(member.getPhone())
                                .build();
    }

    // TODO 변경시 이메일 인증 로직은?
    public void editProfile(EditProfileRequest editProfileRequest){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(memberId))
                                .orElseThrow(MemberDoesNotExistException::new);
        
        if(memberRepository.existsByUsername(editProfileRequest.getMemberUsername())
                        && !member.getUsername().equals(editProfileRequest.getMemberUsername())){
            throw new UseridAlreadyExistException();
        }
        
        member.updateUsername(editProfileRequest.getMemberUsername());
        member.updateName(editProfileRequest.getMemberName());
        member.updateEmail(editProfileRequest.getMemberEmail());
        member.updateIntroduce(editProfileRequest.getMemberIntroduce());
        member.updateWebsite(editProfileRequest.getMemberWebsite());
        member.updatePhone(editProfileRequest.getMemberPhone());
        member.updateGender(Gender.valueOf(editProfileRequest.getMemberGender()));
        memberRepository.save(member);
    }
    
    public List<SearchedMemberDTO> searchMember(String text){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SearchedMemberDTO> result = memberRepository.searchMember(Long.valueOf(memberId), text);
        return result;
    }
    
}
