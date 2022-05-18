package cloneproject.Instagram.domain.follow.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.service.FollowService;
import cloneproject.Instagram.global.result.ResultCode;
import cloneproject.Instagram.global.result.ResultResponse;
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
        final boolean success = followService.follow(followMemberUsername);

        final ResultResponse result = ResultResponse.of(ResultCode.FOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "언팔로우")
    @DeleteMapping("/{followMemberUsername}/follow")
    @ApiImplicitParam(name = "followMemberUsername", value = "언팔로우할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> unfollow(@PathVariable("followMemberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        final boolean success = followService.unfollow(followMemberUsername);

        final ResultResponse result = ResultResponse.of(ResultCode.UNFOLLOW_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "팔로워 삭제")
    @DeleteMapping("/{followMemberUsername}/follower")
    @ApiImplicitParam(name = "followMemberUsername", value = "언팔로우할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> deleteFollower(@PathVariable("followMemberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String followMemberUsername){
        final boolean success = followService.deleteFollower(followMemberUsername);

        final ResultResponse result = ResultResponse.of(ResultCode.DELETE_FOLLOWER_SUCCESS, success);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "팔로잉 목록 조회")
    @GetMapping("/{memberUsername}/following")
    @ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> getFollowings(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        final List<FollowerDto> followings = followService.getFollowings(memberUsername);

        final ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWINGS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @ApiOperation(value = "팔로워 목록 조회")
    @GetMapping("/{memberUsername}/followers")
    @ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
    public ResponseEntity<ResultResponse> getFollowers(@PathVariable("memberUsername") @Validated
                                                @NotBlank(message = "username이 필요합니다") String memberUsername){
        final List<FollowerDto> followings = followService.getFollowers(memberUsername);

        final ResultResponse result = ResultResponse.of(ResultCode.GET_FOLLOWERS_SUCCESS, followings);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

}
