package cloneproject.Instagram.controller;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.UpdatePasswordRequest;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.MemberAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "멤버 인증 API")
@RestController
@RequiredArgsConstructor
public class MemberAuthController {
    
    private final MemberAuthService memberAuthService;

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
        boolean check = memberAuthService.checkUsername(username);
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
        boolean isRegistered = memberAuthService.register(registerRequest);
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
        memberAuthService.sendEmailConfirmation(username, email);
        ResultResponse result = ResultResponse.of(ResultCode.SEND_CONFIRM_EMAIL_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "로그인")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/login")
    public ResponseEntity<ResultResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        JwtDto jwt = memberAuthService.login(loginRequest);
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
        JwtDto jwt = memberAuthService.reisuue(refreshToken);

        ResultResponse result = ResultResponse.of(ResultCode.REISSUE_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
    @ApiOperation(value = "비밀번호 변경")
    @PutMapping(value = "/accounts/password")
    public ResponseEntity<ResultResponse> updatePassword(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        memberAuthService.updatePassword(updatePasswordRequest);
        
        ResultResponse result = ResultResponse.of(ResultCode.UPDATE_PASSWORD_SUCCESS , null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
