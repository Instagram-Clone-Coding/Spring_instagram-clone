package cloneproject.Instagram.controller;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
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
import cloneproject.Instagram.vo.SearchedMemberInfo;
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

    @ApiOperation(value = "username 중복 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @PostMapping(value = "/accounts/check")
    public ResponseEntity<ResultResponse> checkUsername(
                            @RequestParam
                            @Validated 
                            @NotBlank(message = "username을 입력해주세요")
                            @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
                            @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") 
                            String username) {
        boolean check = memberService.checkUsername(username);
        ResultResponse result;
        if(check){
            result = ResultResponse.of(ResultCode.CHECK_USERNAME_GOOD, true);
        }else{
            result = ResultResponse.of(ResultCode.CHECK_USERNAME_BAD, false);
        }   
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "회원가입")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/accounts")
    public ResponseEntity<ResultResponse> register(@Validated @RequestBody RegisterRequest registerRequest) {
        boolean isRegistered = memberService.register(registerRequest);
        ResultResponse result;
        if(isRegistered){
            result = ResultResponse.of(ResultCode.REGISTER_SUCCESS,true);
        }else{
            result = ResultResponse.of(ResultCode.CONFIRM_EMAIL_FAIL, false);
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
    @ApiOperation(value = "인증코드 이메일 전송")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
        @ApiImplicitParam(name = "email", value = "이메일", required = true, example = "aaa@gmail.com")
    })
    @PostMapping(value = "/accounts/email")
    public ResponseEntity<ResultResponse> sendConfirmEmail(
                                    @RequestParam
                                    @Validated
                                    @NotBlank(message = "username을 입력해주세요")
                                    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
                                    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.")
                                    String username,
                                    @RequestParam
                                    @Validated
                                    @NotBlank(message = "이메일을 입력해주세요")
                                    @Email(message = "이메일의 형식이 맞지 않습니다")
                                    String email) {
        memberService.sendEmailConfirmation(username, email);
        ResultResponse result = ResultResponse.of(ResultCode.SEND_CONFIRM_EMAIL_SUCCESS,null);
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
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "refreshToken", value = "refresh 토큰", required = true, example = "AAA.BBB.CCC"),
    })
    @PostMapping(value = "/reissue")
    public ResponseEntity<ResultResponse> reissue(
                                    @Validated
                                    @RequestParam
                                    @NotBlank(message = "Refresh Token은 필수입니다")
                                    String refreshToken) {
        JwtDto jwt = memberService.reisuue(refreshToken);

        ResultResponse result = ResultResponse.of(ResultCode.REISSUE_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "유저 프로필 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "있어도 되고 없어도됨", required = false, example = "Bearer AAA.BBB.CCC"),
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

    @ApiOperation(value = "멤버 검색")
    @ApiImplicitParam(name = "text", value = "검색내용", required = true, example = "dlwl")
    @PostMapping(value = "/search")
    public ResponseEntity<ResultResponse> searchMember(@RequestParam String text) {
        List<SearchedMemberInfo> memberInfos = memberService.searchMember(text);

        ResultResponse result = ResultResponse.of(ResultCode.SEARCH_MEMBER_SUCCESS, memberInfos);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
