package cloneproject.Instagram.service;



import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.dto.member.*;
import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.entity.member.Gender;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.UploadProfileImageFailException;
import cloneproject.Instagram.exception.UseridAlreadyExistException;
import cloneproject.Instagram.repository.FollowRepository;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.post.PostRepository;
import cloneproject.Instagram.repository.specs.MemberSpecification;
import cloneproject.Instagram.util.PostUtil;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.SearchedMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {


    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FollowService followService;
    private final BlockService blockService;
    private final PostRepository postRepository;
    
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username){
        final Member member = memberRepository.findByUsername(username)
        .orElseThrow(MemberDoesNotExistException::new);
        boolean isFollowing, isFollower, isBlocking, isBlocked;
        try{
            final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
            isFollowing = followService.isFollowing(Long.valueOf(memberId), member.getId());
            isFollower = followService.isFollowing(member.getId(), Long.valueOf(memberId));
            isBlocking = blockService.isBlocking(Long.valueOf(memberId), member.getId());
            isBlocked = blockService.isBlocking(member.getId(), Long.valueOf(memberId));
        }catch(Exception e){
            isFollower = false;
            isFollowing = false;
            isBlocking = false;
            isBlocked = false;
        }
        
        UserProfileResponse result = UserProfileResponse.builder()
                                .memberUsername(member.getUsername())
                                .memberName(member.getName())
                                .memberImage(member.getImage())
                                .isFollowing(isFollowing)
                                .isFollower(isFollower)
                                .isBlocking(isBlocking)
                                .isBlocked(isBlocked)
                                .memberPostsCount(postRepository.countByMemberId(member.getId()))
                                .memberFollowersCount(followRepository.countByFollowMemberId(member.getId()))
                                .memberFollowingsCount(followRepository.countByMemberId(member.getId()))
                                .memberIntroduce(member.getIntroduce())
                                .build();
        
        if(isBlocked || isBlocking){
            result.blockedProfile();
        }

        return result;

    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfile(String username){
        final Member member = memberRepository.findByUsername(username)
                                        .orElseThrow(MemberDoesNotExistException::new);

        boolean isBlocked = blockService.isBlocked(username);
        boolean isBlocking = blockService.isBlocking(username);
        
        List<Post> posts = postRepository.findTop3ByMemberIdOrderByUploadDateDesc(member.getId());
        List<MemberPostDTO> postDTOs = posts.stream().map(PostUtil::convertPostToMemberPostDTO)
                                                    .collect(Collectors.toList());

        MiniProfileResponse result = MiniProfileResponse.builder()
                                .memberUsername(member.getUsername())
                                .memberImage(member.getImage())
                                .memberName(member.getName())
                                .memberWebsite(member.getWebsite())
                                .isFollowing(followService.isFollowing(username))
                                .isFollower(followService.isFollower(username))
                                .isBlocking(isBlocking)
                                .isBlocked(isBlocked)
                                .memberPostsCount(postRepository.countByMemberId(member.getId()))
                                .memberFollowersCount(followRepository.countByFollowMemberId(member.getId()))
                                .memberFollowingsCount(followRepository.countByMemberId(member.getId()))
                                .memberPosts(postDTOs)
                                .build();

        if(isBlocked || isBlocking){
            result.blockedProfile();
        }

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

        Image image;
        try{
            image = s3Uploader.uploadImage(uploadedImage, "member");
        }catch(IOException e){
            throw new UploadProfileImageFailException();
        }
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

    public SearchedMemberInfo convertMemberToSearchedMemberInfo(Member member){
        return SearchedMemberInfo.builder()
                                .username(member.getUsername())
                                .name(member.getName())
                                .image(member.getImage())
                                .isFollwing(followService.isFollowing(member.getUsername()))
                                .build();
    }

    public List<SearchedMemberInfo> searchMember(String text){
        List<Member> memberList = memberRepository.findAll(MemberSpecification.containsTextInUsernameOrName(text));
        List<SearchedMemberInfo> result = memberList.stream()
                                                    .map(this::convertMemberToSearchedMemberInfo)
                                                    .collect(Collectors.toList());
        return result;
    }
}
