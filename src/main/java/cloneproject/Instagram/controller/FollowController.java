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

import cloneproject.Instagram.dto.member.FollowerDTO;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "팔로우 API")
@RestController
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;

    @ApiOperation(value = "팔로우")
    @PostMapping("/{followMemberUsername}/follow")
    @ApiImplicitParam(name = "followMemberUsername", value = "팔로우할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") @Validated
                                                 @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        boolean success = followService.follow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.FOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "언팔로우")
    @PostMapping("/{followMemberUsername}/unfollow")
    @ApiImplicitParam(name = "followMemberUsername", value = "언팔로우할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> unfollow(@PathVariable("followMemberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        boolean success = followService.unfollow(followMemberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.UNFOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "팔로잉 목록 조회")
    @GetMapping("/{memberUsername}/following")
    @ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> getFollowings(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        List<FollowerDTO> followings = followService.getFollowings(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWINGS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "팔로워 목록 조회")
    @GetMapping("/{memberUsername}/followers")
    @ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> getFollowers(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        List<FollowerDTO> followings = followService.getFollowers(memberUsername);
        
        ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWERS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
