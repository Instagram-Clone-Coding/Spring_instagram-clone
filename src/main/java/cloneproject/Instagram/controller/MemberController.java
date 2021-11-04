package cloneproject.Instagram.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.MessageResponse;
import cloneproject.Instagram.dto.RegisterRequest;
import cloneproject.Instagram.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @PostMapping(value = "/accounts")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest registerRequest) {
        memberService.register(registerRequest);

        MessageResponse response = new MessageResponse("회원가입 되었습니다.");
        return new ResponseEntity<MessageResponse>(response,HttpStatus.OK);
    }

}
