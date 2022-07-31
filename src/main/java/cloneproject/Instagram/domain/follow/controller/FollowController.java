package cloneproject.Instagram.domain.follow.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.service.FollowService;
import cloneproject.Instagram.global.result.ResultResponse;

@Api(tags = "팔로우 API")
@RestController
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@ApiOperation(value = "팔로우")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F001 - 회원 팔로우를 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.\n"
			+ "F001 - 이미 팔로우한 유저입니다.\n"
			+ "F003 - 자기 자신을 팔로우 할 수 없습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "followMemberUsername", value = "팔로우할 계정의 username", required = true, example = "dlwlrma")
	@PostMapping("/{followMemberUsername}/follow")
	public ResponseEntity<ResultResponse> follow(@PathVariable("followMemberUsername") @Validated
	@NotBlank(message = "username이 필요합니다") String followMemberUsername) {
		final boolean success = followService.follow(followMemberUsername);

		return ResponseEntity.ok(ResultResponse.of(FOLLOW_SUCCESS, success));
	}

	@ApiOperation(value = "언팔로우")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F002 - 회원 언팔로우를 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.\n"
			+ "F002 - 팔로우하지 않은 유저는 언팔로우 할 수 없습니다.\n"
			+ "F004 - 자기 자신을 언팔로우 할 수 없습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "followMemberUsername", value = "언팔로우할 계정의 username", required = true, example = "dlwlrma")
	@DeleteMapping("/{followMemberUsername}/follow")
	public ResponseEntity<ResultResponse> unfollow(@PathVariable("followMemberUsername") @Validated
	@NotBlank(message = "username이 필요합니다") String followMemberUsername) {
		final boolean success = followService.unfollow(followMemberUsername);

		return ResponseEntity.ok(ResultResponse.of(UNFOLLOW_SUCCESS, success));
	}

	@ApiOperation(value = "팔로워 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F005 - 팔로워 삭제를 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.\n"
			+ "F005 - 팔로워 삭제할 수 없는 대상입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "followMemberUsername", value = "팔로워 삭제할 계정의 username", required = true, example = "dlwlrma")
	@DeleteMapping("/{followMemberUsername}/follower")
	public ResponseEntity<ResultResponse> deleteFollower(@PathVariable("followMemberUsername") @Validated
	@NotBlank(message = "username이 필요합니다") String followMemberUsername) {
		final boolean success = followService.deleteFollower(followMemberUsername);

		return ResponseEntity.ok(ResultResponse.of(DELETE_FOLLOWER_SUCCESS, success));
	}

	@ApiOperation(value = "팔로잉 목록 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F003 - 회원 팔로잉 목록을 조회하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
	@GetMapping("/{memberUsername}/following")
	public ResponseEntity<ResultResponse> getFollowings(@PathVariable("memberUsername") @Validated
	@NotBlank(message = "username이 필요합니다") String memberUsername) {
		final List<FollowerDto> followings = followService.getFollowings(memberUsername);

		return ResponseEntity.ok(ResultResponse.of(GET_FOLLOWINGS_SUCCESS, followings));
	}

	@ApiOperation(value = "팔로워 목록 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F004 - 회원 팔로워 목록을 조회하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "memberUsername", value = "조회 할 계정의 username", required = true, example = "dlwlrma")
	@GetMapping("/{memberUsername}/followers")
	public ResponseEntity<ResultResponse> getFollowers(@PathVariable("memberUsername") @Validated
	@NotBlank(message = "username이 필요합니다") String memberUsername) {
		final List<FollowerDto> followings = followService.getFollowers(memberUsername);

		return ResponseEntity.ok(ResultResponse.of(GET_FOLLOWERS_SUCCESS, followings));
	}

}
