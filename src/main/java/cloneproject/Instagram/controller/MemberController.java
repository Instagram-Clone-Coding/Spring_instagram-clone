package cloneproject.Instagram.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.JwtDto;
import cloneproject.Instagram.dto.LoginRequest;
import cloneproject.Instagram.dto.MessageResponse;
import cloneproject.Instagram.dto.RegisterRequest;
import cloneproject.Instagram.dto.ReissueRequest;
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
    public ResponseEntity<MessageResponse> register(@Validated @RequestBody RegisterRequest registerRequest) {
        memberService.register(registerRequest);

        MessageResponse response = new MessageResponse("회원가입 되었습니다.");
        return new ResponseEntity<MessageResponse>(response,HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<JwtDto> login(@Validated @RequestBody LoginRequest loginRequest) {
        JwtDto result = memberService.login(loginRequest);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<JwtDto> reissue(@Validated @RequestBody ReissueRequest reissueRequest) {
        JwtDto result = memberService.reisuue(reissueRequest);
        
        return ResponseEntity.ok(result);
    }


    // ! login 권한 테스트를 위해 임시로 만든 메서드입니다. 추후에 삭제
    @GetMapping(value = "/info")
    public String info() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member result = memberService.info(memberId);
        
        return result.getUsername();
    }
}
