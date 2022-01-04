package cloneproject.Instagram.controller;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cloneproject.Instagram.dto.member.EditProfileRequest;
import cloneproject.Instagram.dto.member.EditProfileResponse;
import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.MiniProfileResponse;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.ReissueRequest;
import cloneproject.Instagram.dto.member.UpdatePasswordRequest;
import cloneproject.Instagram.dto.member.UserProfileResponse;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Api(tags = "멤버 API")
@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @ApiOperation(value = "회원가입")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/accounts")
    public ResponseEntity<ResultResponse> register(@Validated @RequestBody RegisterRequest registerRequest) {
        memberService.register(registerRequest);
        ResultResponse result = ResultResponse.of(ResultCode.REGISTER_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "로그인")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/login")
    public ResponseEntity<ResultResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        JwtDto jwt = memberService.login(loginRequest);

        ResultResponse result = ResultResponse.of(ResultCode.LOGIN_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/reissue")
    public ResponseEntity<ResultResponse> reissue(@Validated @RequestBody ReissueRequest reissueRequest) {
        JwtDto jwt = memberService.reisuue(reissueRequest);

        ResultResponse result = ResultResponse.of(ResultCode.REISSUE_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "유저 프로필 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @GetMapping(value = "/accounts/{username}")
    public ResponseEntity<ResultResponse> getUserProfile(@PathVariable("username") String username){
        UserProfileResponse userProfileResponse = memberService.getUserProfile(username);

        ResultResponse result = ResultResponse.of(ResultCode.GET_USERPROFILE_SUCCESS, userProfileResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "미니 프로필 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @GetMapping(value = "/accounts/{username}/mini")
    public ResponseEntity<ResultResponse> getMiniProfile(@PathVariable("username") String username){
        MiniProfileResponse miniProfileResponse = memberService.getMiniProfile(username);

        ResultResponse result = ResultResponse.of(ResultCode.GET_MINIPROFILE_SUCCESS, miniProfileResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "회원 프로필 사진 업로드")
    @PostMapping(value = "/accounts/image")
    public ResponseEntity<ResultResponse> uploadImage(@RequestParam MultipartFile uploadedImage) {
        memberService.uploadMemberImage(uploadedImage);

        ResultResponse result = ResultResponse.of(ResultCode.UPLOAD_MEMBER_IMAGE_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "회원 프로필 사진 삭제")
    @DeleteMapping(value = "/accounts/image")
    public ResponseEntity<ResultResponse> deleteImage() {
        memberService.deleteMemberImage();

        ResultResponse result = ResultResponse.of(ResultCode.DELETE_MEMBER_IMAGE_SUCCESS ,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "회원 프로필 수정정보 조회")
    @GetMapping(value = "/accounts/edit")
    public ResponseEntity<ResultResponse> getMemberEdit() {
        EditProfileResponse editProfileResponse = memberService.getEditProfile();
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_EDIT_PROFILE_SUCCESS ,editProfileResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "회원 프로필 수정")
    @PutMapping(value = "/accounts/edit")
    public ResponseEntity<ResultResponse> editProfile(@Validated @RequestBody EditProfileRequest editProfileRequest) {
        memberService.editProfile(editProfileRequest);
        
        ResultResponse result = ResultResponse.of(ResultCode.EDIT_PROFILE_SUCCESS , null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "비밀번호 변경")
    @PutMapping(value = "/accounts/password")
    public ResponseEntity<ResultResponse> updatePassword(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        memberService.updatePassword(updatePasswordRequest);
        
        ResultResponse result = ResultResponse.of(ResultCode.UPDATE_PASSWORD_SUCCESS , null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
}
