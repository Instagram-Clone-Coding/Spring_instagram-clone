package cloneproject.Instagram.domain.hashtag.controller;

import cloneproject.Instagram.domain.hashtag.service.HashtagService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cloneproject.Instagram.global.result.ResultCode.*;

import javax.validation.constraints.NotBlank;

@Api(tags = "해시태그 API")
@Validated
@RestController
@RequiredArgsConstructor
public class HashtagController {

	private final HashtagService hashtagService;

	@ApiOperation(value = "해시태그 팔로우")
	@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	@PostMapping("/hashtags/follow")
	public ResponseEntity<ResultResponse> followHashtag(
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		hashtagService.followHashtag(hashtag);

		return ResponseEntity.ok(ResultResponse.of(FOLLOW_HASHTAG_SUCCESS));
	}

	@ApiOperation(value = "해시태그 언팔로우")
	@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	@DeleteMapping("/hashtags/follow")
	public ResponseEntity<ResultResponse> unfollowHashtag(
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		hashtagService.unfollowHashtag(hashtag);

		return ResponseEntity.ok(ResultResponse.of(UNFOLLOW_HASHTAG_SUCCESS));
	}

}
