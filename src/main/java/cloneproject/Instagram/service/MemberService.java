package cloneproject.Instagram.service;



import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.dto.member.*;
import cloneproject.Instagram.entity.member.Gender;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.redis.EmailCode;
import cloneproject.Instagram.exception.AccountDoesNotMatchException;
import cloneproject.Instagram.exception.InvalidJwtException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.UploadProfileImageFailException;
import cloneproject.Instagram.exception.UseridAlreadyExistException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.specs.MemberSpecification;
import cloneproject.Instagram.util.JwtUtil;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.RefreshToken;
import cloneproject.Instagram.vo.SearchedMemberInfo;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtUtil jwtUtil;
    private final EmailCodeService emailCodeService;

    private final MemberRepository memberRepository;
    private final FollowService followService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private final S3Uploader s3Uploader;

    @Transactional(readOnly = true)
    public boolean checkUsername(String username){
        if(memberRepository.existsByUsername(username)){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean register(RegisterRequest registerRequest){
        if(memberRepository.existsByUsername(registerRequest.getUsername())){
            throw new UseridAlreadyExistException();
        }
        String username = registerRequest.getUsername();
        EmailCode emailCode = emailCodeService.findByUsername(username);
        if(emailCode == null || !emailCode.getCode().equals(registerRequest.getCode())){
            return false;
        }
        emailCodeService.deleteEmailCode(emailCode);
        Member member = registerRequest.convert();
        String encryptedPassword = bCryptPasswordEncoder.encode(member.getPassword());
        member.setEncryptedPassword(encryptedPassword);
        memberRepository.save(member);
        return true;
    }

    public void sendEmailConfirmation(String username, String email){
        if(memberRepository.existsByUsername(username)){
            throw new UseridAlreadyExistException();
        }
        emailCodeService.sendEmailConfirmationCode(username, email);
    }

    @Transactional
    public JwtDto login(LoginRequest loginRequest){
        try{
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);
            RefreshToken refreshToken = RefreshToken.builder()
                                                .value(jwtDto.getRefreshToken())
                                                .build();
            Member member = memberRepository.findById(Long.valueOf(authentication.getName()))
                                            .orElseThrow(MemberDoesNotExistException::new);
            member.setRefreshToken(refreshToken);
            memberRepository.save(member);
            
            return jwtDto;
        }catch(BadCredentialsException e){
            throw new AccountDoesNotMatchException();
        }
    }

    @Transactional
    public JwtDto reisuue(ReissueRequest reissueRequest){
        String accessTokenString = reissueRequest.getAccessToken();
        String refreshTokenString = reissueRequest.getRefreshToken();
        if(!jwtUtil.validateRefeshJwt(refreshTokenString)){
            throw new InvalidJwtException();
        }
        Authentication authentication;
        try{
            authentication = jwtUtil.getAuthentication(accessTokenString);
        } catch(JwtException e){
            throw new InvalidJwtException();
        }
        Member member = memberRepository.findById(Long.valueOf(authentication.getName()))
                                        .orElseThrow(MemberDoesNotExistException::new);
        RefreshToken refreshToken = member.getRefreshToken();
        if(!refreshToken.getValue().equals(refreshTokenString)){
            throw new InvalidJwtException();
        }
        
        JwtDto jwtDto = jwtUtil.generateTokenDto(authentication);

        refreshToken.updateTokenValue(jwtDto.getRefreshToken());
        memberRepository.save(member);

        return jwtDto;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username){
        final Member member = memberRepository.findByUsername(username)
                                        .orElseThrow(MemberDoesNotExistException::new);
        return UserProfileResponse.builder()
                                .memberUsername(member.getUsername())
                                .memberName(member.getName())
                                .memberImage(member.getImage())
                                //TODO 팔로잉여부, 팔로워여부
                                .memberFollowersCount(followService.getFollowersCount(username))
                                .memberFollowingsCount(followService.getFollowingsCount(username))
                                .memberIntroduce(member.getIntroduce())
                                .build();
    }

    @Transactional(readOnly = true)
    public MiniProfileResponse getMiniProfile(String username){
        final Member member = memberRepository.findByUsername(username)
                                        .orElseThrow(MemberDoesNotExistException::new);
        return MiniProfileResponse.builder()
                                .memberUsername(member.getUsername())
                                .memberImage(member.getImage())
                                .memberName(member.getName())
                                .memberWebsite(member.getWebsite())
                                .memberPostCount(0) // TODO 추후에 수정
                                .memberFollowersCount(followService.getFollowersCount(username))
                                .memberFollowingsCount(followService.getFollowingsCount(username))
                                .build();
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

    @Transactional
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(memberId))
                                    .orElseThrow(MemberDoesNotExistException::new);
        boolean oldPasswordCorrect = bCryptPasswordEncoder.matches(updatePasswordRequest.getOldPassword()
                                                                , member.getPassword());
        if(!oldPasswordCorrect){
            throw new AccountDoesNotMatchException();
        }
        String encryptedPassword = bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword());
        member.setEncryptedPassword(encryptedPassword);              
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
