package cloneproject.Instagram.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.JwtDto;
import cloneproject.Instagram.dto.LoginRequest;
import cloneproject.Instagram.dto.RegisterRequest;
import cloneproject.Instagram.dto.ReissueRequest;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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


    // ! login 권한 테스트를 위해 임시로 만든 메서드입니다. 추후에 삭제
    @GetMapping(value = "/info")
    public String info() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member result = memberService.info(memberId);
        
        return result.getUsername();
    }
}
