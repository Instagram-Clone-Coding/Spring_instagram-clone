package cloneproject.Instagram.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.FollowService;
import cloneproject.Instagram.vo.UsernameWithImage;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;

    @PostMapping("/{followMemberUsername}/follow")
    public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") String followMemberUsername){
        boolean success = followService.follow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.FOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/{followMemberUsername}/unfollow")
    public ResponseEntity<ResultResponse> unfollow(@PathVariable("followMemberUsername") String followMemberUsername){
        boolean success = followService.unfollow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.UNFOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{memberUsername}/following")
    public ResponseEntity<ResultResponse> getFollowings(@PathVariable("memberUsername") String memberUsername){
        List<UsernameWithImage> followings = followService.getFollowings(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWINGS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{memberUsername}/followers")
    public ResponseEntity<ResultResponse> getFollowers(@PathVariable("memberUsername") String memberUsername){
        List<UsernameWithImage> followings = followService.getFollowers(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWERS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
