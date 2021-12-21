package cloneproject.Instagram.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.FollowService;
import cloneproject.Instagram.vo.FollowerInfo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;

    @PostMapping("/{followMemberUsername}/follow")
    public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") @Validated
                                                 @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        boolean success = followService.follow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.FOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @PostMapping("/{followMemberUsername}/unfollow")
    public ResponseEntity<ResultResponse> unfollow(@PathVariable("followMemberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        boolean success = followService.unfollow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.UNFOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{memberUsername}/following")
    public ResponseEntity<ResultResponse> getFollowings(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        List<FollowerInfo> followings = followService.getFollowings(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWINGS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/{memberUsername}/followers")
    public ResponseEntity<ResultResponse> getFollowers(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        List<FollowerInfo> followings = followService.getFollowers(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWERS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
