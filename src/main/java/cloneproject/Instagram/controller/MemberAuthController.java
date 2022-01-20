package cloneproject.Instagram.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.SendConfirmationEmailRequest;
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
    private final int REFRESH_TOKEN_EXPIRES = 60*60*24*7; // 7일

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
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/accounts/email")
    public ResponseEntity<ResultResponse> sendConfirmEmail(@Validated @RequestBody SendConfirmationEmailRequest sendConfirmationEmailRequest) {
        memberAuthService.sendEmailConfirmation(sendConfirmationEmailRequest.getUsername(), sendConfirmationEmailRequest.getEmail());
        ResultResponse result = ResultResponse.of(ResultCode.SEND_CONFIRM_EMAIL_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "로그인")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/login")
    public ResponseEntity<ResultResponse> login(@Validated @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        JwtDto jwt = memberAuthService.login(loginRequest);

        Cookie cookie = new Cookie("refreshToken", jwt.getRefreshToken());
        jwt.blindRefreshToken();

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
    
        response.addCookie(cookie);
        
        ResultResponse result = ResultResponse.of(ResultCode.LOGIN_SUCCESS, jwt);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/reissue")
    public ResponseEntity<ResultResponse> reissue(
                                @CookieValue(value="refreshToken", required = true)
                                Cookie refreshCookie, HttpServletResponse response
                            ) {
        JwtDto jwt = memberAuthService.reisuue(refreshCookie.getValue());

        Cookie cookie = new Cookie("refreshToken", jwt.getRefreshToken());
        jwt.blindRefreshToken();

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
    
        response.addCookie(cookie);

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
