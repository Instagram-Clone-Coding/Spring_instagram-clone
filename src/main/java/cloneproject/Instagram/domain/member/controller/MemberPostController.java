package cloneproject.Instagram.domain.member.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.member.service.MemberPostService;
import cloneproject.Instagram.global.result.ResultCode;
import cloneproject.Instagram.global.result.ResultResponse;

@Api(tags = "멤버 게시물 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/accounts")
public class MemberPostController {

	private final MemberPostService memberPostService;

	@ApiOperation(value = "멤버 게시물 15개 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP001 - 회원의 최근 게시물 15개 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@GetMapping("/{username}/posts/recent")
	public ResponseEntity<ResultResponse> getRecent15Posts(@PathVariable("username") String username) {
		final List<MemberPostDto> postList = memberPostService.getRecent15PostDtos(username);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT15_MEMBER_POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "멤버 게시물 페이징 조회(무한스크롤)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP002 - 회원의 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
	})
	@GetMapping("/{username}/posts")
	public ResponseEntity<ResultResponse> getPostPage(@PathVariable("username") String username,
		@Min(1) @RequestParam int page) {
		final Page<MemberPostDto> postPage = memberPostService.getMemberPostDtos(username, 3, page);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_POSTS_SUCCESS, postPage));
	}

	@ApiOperation(value = "로그인 없이 멤버 게시물 15개 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP001 - 회원의 최근 게시물 15개 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@GetMapping("/{username}/posts/recent/without")
	public ResponseEntity<ResultResponse> getRecent15PostsWithoutLogin(@PathVariable("username") String username) {
		final List<MemberPostDto> postList = memberPostService.getRecent15PostDtosWithoutLogin(username);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT15_MEMBER_POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "로그인 없이 멤버 게시물 페이징 조회(무한스크롤)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP002 - 회원의 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다.")
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
	})
	@GetMapping("/{username}/posts/without")
	public ResponseEntity<ResultResponse> getPostPageWithoutLogin(@PathVariable("username") String username,
		@Min(1) @RequestParam int page) {
		final Page<MemberPostDto> postPage = memberPostService.getMemberPostDtosWithoutLogin(username, 3, page);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_POSTS_SUCCESS, postPage));
	}

	// ============== 저장 ================
	@ApiOperation(value = "멤버 저장한 게시물 15개 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP003 - 회원의 최근 저장한 게시물 15개 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@GetMapping("/posts/saved/recent")
	public ResponseEntity<ResultResponse> getRecent15SavedPosts() {
		final List<MemberPostDto> postList = memberPostService.getRecent15SavedPostDtos();

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT15_MEMBER_SAVED_POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "멤버 저장한 게시물 페이징 조회(무한스크롤)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP004 - 회원의 저장한 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@GetMapping("/posts/saved")
	@ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
	public ResponseEntity<ResultResponse> getSavedPostPage(@Min(1) @RequestParam int page) {
		final Page<MemberPostDto> postPage = memberPostService.getMemberSavedPostDtos(3, page);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_SAVED_POSTS_SUCCESS, postPage));
	}

	// ============== 태그 ================
	@ApiOperation(value = "멤버 태그된 게시물 15개 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP005 - 회원의 최근 태그된 게시물 15개 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
	@GetMapping("/{username}/posts/tagged/recent")
	public ResponseEntity<ResultResponse> getRecent10TaggedPosts(@PathVariable("username") String username) {
		final List<MemberPostDto> postList = memberPostService.getRecent15TaggedPostDtos(username);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_RECENT15_MEMBER_TAGGED_POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "멤버 태그된 게시물 페이징 조회(무한스크롤)")
	@ApiResponses({
		@ApiResponse(code = 200, message = "MP006 - 회원의 태그된 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "M001 - 존재 하지 않는 유저입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다.")
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
		@ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
	})
	@GetMapping("/{username}/posts/tagged")
	public ResponseEntity<ResultResponse> getTaggedPostPage(@PathVariable("username") String username,
		@Min(1) @RequestParam int page) {
		final Page<MemberPostDto> postPage = memberPostService.getMemberTaggedPostDtos(username, 3, page);

		return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_MEMBER_TAGGED_POSTS_SUCCESS, postPage));
	}

}
