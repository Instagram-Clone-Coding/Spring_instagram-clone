package cloneproject.Instagram.domain.hashtag.controller;

import cloneproject.Instagram.domain.hashtag.service.HashtagService;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
	@ApiResponses({
		@ApiResponse(code = 200, message = "H002 - 해시태그 팔로우에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "G007 - 지원하지 않는 이미지 타입입니다.\n"
			+ "G008 - 변환할 수 없는 파일입니다.\n"
			+ "H001 - 존재하지 않는 해시태그 입니다.\n"
			+ "H002 - 해시태그 팔로우에 실패했습니다.\n"
			+ "H004 - 해시태그는 #으로 시작해야 합니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	@PostMapping("/hashtags/follow")
	public ResponseEntity<ResultResponse> followHashtag(
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		hashtagService.followHashtag(hashtag);

		return ResponseEntity.ok(ResultResponse.of(FOLLOW_HASHTAG_SUCCESS));
	}

	@ApiOperation(value = "해시태그 언팔로우")
	@ApiResponses({
		@ApiResponse(code = 200, message = "H003 - 해시태그 언팔로우에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "G007 - 지원하지 않는 이미지 타입입니다.\n"
			+ "G008 - 변환할 수 없는 파일입니다.\n"
			+ "H001 - 존재하지 않는 해시태그 입니다.\n"
			+ "H003 - 해시태그 언팔로우에 실패했습니다.\n"
			+ "H004 - 해시태그는 #으로 시작해야 합니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	@DeleteMapping("/hashtags/follow")
	public ResponseEntity<ResultResponse> unfollowHashtag(
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		hashtagService.unfollowHashtag(hashtag);

		return ResponseEntity.ok(ResultResponse.of(UNFOLLOW_HASHTAG_SUCCESS));
	}

}
