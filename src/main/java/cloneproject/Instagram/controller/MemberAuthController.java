package cloneproject.Instagram.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.member.JwtDto;
import cloneproject.Instagram.dto.member.JwtResponse;
import cloneproject.Instagram.dto.member.LoginRequest;
import cloneproject.Instagram.dto.member.RegisterRequest;
import cloneproject.Instagram.dto.member.ResetPasswordRequest;
import cloneproject.Instagram.dto.member.SendConfirmationEmailRequest;
import cloneproject.Instagram.dto.member.UpdatePasswordRequest;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.exception.InvalidJwtException;
import cloneproject.Instagram.service.MemberAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "멤버 인증 API")
@Validated
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
    @GetMapping(value = "/accounts/check")
    public ResponseEntity<ResultResponse> checkUsername(
                            @RequestParam
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

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("bullien.com");
    
        response.addCookie(cookie);

        JwtResponse jwtResponse = JwtResponse.builder()
                                            .type(jwt.getType())
                                            .accessToken(jwt.getAccessToken())
                                            .build();

        
        ResultResponse result = ResultResponse.of(ResultCode.LOGIN_SUCCESS, jwtResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "토큰 재발급")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/reissue")
    public ResponseEntity<ResultResponse> reissue(
                                @CookieValue(value="refreshToken", required = false)
                                Cookie refreshCookie, HttpServletResponse response
                            ) {
        if(refreshCookie == null){
            throw new InvalidJwtException();
        }
        JwtDto jwt = memberAuthService.reisuue(refreshCookie.getValue());
        Cookie cookie = new Cookie("refreshToken", jwt.getRefreshToken());

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("bullien.com");
    
        response.addCookie(cookie);

        JwtResponse jwtResponse = JwtResponse.builder()
                                            .type(jwt.getType())
                                            .accessToken(jwt.getAccessToken())
                                            .build();

        ResultResponse result = ResultResponse.of(ResultCode.REISSUE_SUCCESS, jwtResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
    @ApiOperation(value = "비밀번호 변경")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PutMapping(value = "/accounts/password")
    public ResponseEntity<ResultResponse> updatePassword(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        memberAuthService.updatePassword(updatePasswordRequest);
        
        ResultResponse result = ResultResponse.of(ResultCode.UPDATE_PASSWORD_SUCCESS , null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }
    
    @ApiOperation(value = "비밀번호변경 이메일 전송")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @PostMapping(value = "/accounts/password/email")
    public ResponseEntity<ResultResponse> sendResetPasswordCode(
        @RequestParam
        @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
        @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") 
        String username) {
        memberAuthService.sendResetPasswordCode(username);
        ResultResponse result = ResultResponse.of(ResultCode.SEND_RESET_PASSWORD_EMAIL_SUCCESS,null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "코드를 통한 비밀번호 재설정")
    @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " ")
    @PostMapping(value = "/accounts/password/reset")
    public ResponseEntity<ResultResponse> resetPassword(@Validated @RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletResponse response) {
        JwtDto jwt = memberAuthService.resetPassword(resetPasswordRequest);

        Cookie cookie = new Cookie("refreshToken", jwt.getRefreshToken());

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("bullien.com");
    
        response.addCookie(cookie);

        JwtResponse jwtResponse = JwtResponse.builder()
                                            .type(jwt.getType())
                                            .accessToken(jwt.getAccessToken())
                                            .build();
        
        ResultResponse result = ResultResponse.of(ResultCode.RESET_PASSWORD_SUCCESS, jwtResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "코드를 통한 로그인")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
        @ApiImplicitParam(name = "code", value = "인증코드", required = true, example = "AAAA1234...")
    })
    @PostMapping(value = "/accounts/login/recovery")
    public ResponseEntity<ResultResponse> loginWithCode(
        @RequestParam
        @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
        @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") 
        String username,
        @RequestParam
        @Length(max = 30, min = 30, message = "인증코드는 30자리 입니다.")
        String code,
        HttpServletResponse response) {
        JwtDto jwt = memberAuthService.loginWithCode(username, code);

        Cookie cookie = new Cookie("refreshToken", jwt.getRefreshToken());

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("bullien.com");
    
        response.addCookie(cookie);

        JwtResponse jwtResponse = JwtResponse.builder()
                                            .type(jwt.getType())
                                            .accessToken(jwt.getAccessToken())
                                            .build();
        
        ResultResponse result = ResultResponse.of(ResultCode.LOGIN_WITH_CODE_SUCCESS, jwtResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }


    @ApiOperation(value = "비밀번호 재설정 코드 만료시키기")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "불필요", required = false, example = " "),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
    })
    @DeleteMapping(value = "/accounts/login/recovery")
    public ResponseEntity<ResultResponse> expireResetPasswordCode(
        @RequestParam
        @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다")
        @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "username엔 대소문자, 숫자만 사용할 수 있습니다.") 
        String username) {
        memberAuthService.expireResetPasswordCode(username);
        ResultResponse result = ResultResponse.of(ResultCode.EXPIRE_RESET_PASSWORD_CODE_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
