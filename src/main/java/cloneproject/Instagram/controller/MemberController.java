package cloneproject.Instagram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.RegisterRequest;
import cloneproject.Instagram.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// TODO 테스트

@RestController
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;

    @PostMapping(value = "/accounts")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        memberService.register(registerRequest);
        return new ResponseEntity<String>("SUCCESS!",HttpStatus.OK);
    }
    
}
