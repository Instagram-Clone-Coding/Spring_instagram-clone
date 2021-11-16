package cloneproject.Instagram.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.ReissueRequest;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
// TODO member api
//  프로필 수정 
//  프로필 수정정보 조회
//  프로필 조회 o
//  이미지 등록 o
//  이미지 삭제
@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @PostMapping(value = "/accounts")
    public ResponseEntity<ResultResponse> register(@Validated @RequestBody RegisterRequest registerRequest) {
        memberService.register(registerRequest);
        ResultResponse result = ResultResponse.of(ResultCode.REGISTER_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ResultResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        JwtDto jwt = memberService.login(loginRequest);

        ResultResponse result = ResultResponse.of(ResultCode.LOGIN_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<ResultResponse> reissue(@Validated @RequestBody ReissueRequest reissueRequest) {
        JwtDto jwt = memberService.reisuue(reissueRequest);

        ResultResponse result = ResultResponse.of(ResultCode.REISSUE_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<ResultResponse> getUserProfile(@PathVariable("username") String username){
        UserProfileResponse userProfileResponse = memberService.getUserProfile(username);

        ResultResponse result = ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, userProfileResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    // TODO UPLOAD IMAGE SUCCESS 수정.(POST와 MEMBER 구분)
    @PostMapping(value = "/accounts/image")
    public ResponseEntity<ResultResponse> uploadImage(@RequestParam MultipartFile uploadedImage) {
        Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        memberService.uploadMemberImage(memberId, uploadedImage);

        ResultResponse result = ResultResponse.of(ResultCode.UPLOAD_MEMBER_IMAGE_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    // ! login 권한 테스트를 위해 임시로 만든 메서드입니다. 추후에 변경
    @GetMapping(value = "/accounts/edit")
    public String getEdit() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member result = memberService.info(memberId);
        
        return result.getUsername();
    }
}
