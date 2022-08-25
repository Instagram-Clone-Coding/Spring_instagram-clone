package cloneproject.Instagram.domain.feed.controller;

import static cloneproject.Instagram.global.result.ResultCode.*;
import static cloneproject.Instagram.global.util.ConstantUtils.*;
import static org.springframework.http.MediaType.*;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

import cloneproject.Instagram.domain.feed.dto.PostDto;
import cloneproject.Instagram.domain.feed.dto.PostUploadRequest;
import cloneproject.Instagram.domain.feed.dto.PostUploadResponse;
import cloneproject.Instagram.domain.feed.service.PostService;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.global.result.ResultResponse;

@Api(tags = "게시물 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

	private static final int BASE_POST_SIZE = 10;

	private final PostService postService;

	@ApiOperation(value = "게시물 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
	@ApiResponses({
		@ApiResponse(code = 200, message = "F001 - 게시물 업로드에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G007 - 지원하지 않는 이미지 타입입니다.\n"
			+ "G008 - 변환할 수 없는 파일입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@PostMapping
	public ResponseEntity<ResultResponse> uploadPost(@Valid @ModelAttribute PostUploadRequest request) {
		final PostUploadResponse response = postService.upload(request);

		return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 목록 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F003 - 게시물 목록 페이지 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "page", value = "게시물 page", example = "1", required = true)
	@GetMapping
	public ResponseEntity<ResultResponse> getPostPage(@RequestParam int page) {
		page = (page == BASE_PAGE_NUMBER ? BASE_PAGE_NUMBER : page - PAGE_ADJUSTMENT_VALUE) + BASE_POST_SIZE;
		final Page<PostDto> postPage = postService.getPostDtoPage(POST_INFINITY_SCROLL_SIZE, page);

		return ResponseEntity.ok(ResultResponse.of(FIND_POST_PAGE_SUCCESS, postPage));
	}

	@ApiOperation(value = "최근 게시물 10개 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F005 - 최근 게시물 10개 조회에 성공하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@GetMapping("/recent")
	public ResponseEntity<ResultResponse> getRecent10Posts() {
		final List<PostDto> postList = postService.getPostDtoPage(BASE_POST_SIZE, BASE_PAGE_NUMBER).getContent();

		return ResponseEntity.ok(ResultResponse.of(FIND_RECENT10POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "게시물 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F002 - 게시물 삭제에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F002 - 게시물 게시자만 삭제할 수 있습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping
	public ResponseEntity<ResultResponse> deletePost(@RequestParam Long postId) {
		postService.delete(postId);

		return ResponseEntity.ok(ResultResponse.of(DELETE_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F004 - 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@GetMapping("/{postId}")
	public ResponseEntity<ResultResponse> getPost(@PathVariable Long postId) {
		final PostDto response = postService.getPostDto(postId);

		return ResponseEntity.ok(ResultResponse.of(FIND_POST_SUCCESS, response));
	}

	@ApiOperation(value = "로그인 없이 게시물 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F004 - 게시물 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@GetMapping("/{postId}/without")
	public ResponseEntity<ResultResponse> getPostWithoutLogin(@PathVariable Long postId) {
		final PostDto response = postService.getPostDtoWithoutLogin(postId);

		return ResponseEntity.ok(ResultResponse.of(FIND_POST_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 좋아요")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F006 - 게시물 좋아요에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F004 - 해당 게시물에 이미 좋아요를 누른 회원입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@PostMapping("/like")
	public ResponseEntity<ResultResponse> likePost(@RequestParam Long postId) {
		postService.likePost(postId);

		return ResponseEntity.ok(ResultResponse.of(LIKE_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 좋아요 해제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F007 - 게시물 좋아요 해제에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F003 - 해당 게시물에 좋아요를 누르지 않은 회원입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping("/like")
	public ResponseEntity<ResultResponse> unlikePost(@RequestParam Long postId) {
		postService.unlikePost(postId);

		return ResponseEntity.ok(ResultResponse.of(UN_LIKE_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 좋아요한 회원 목록 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F014 - 게시물에 좋아요한 회원 목록 페이지 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F014 - 좋아요 목록을 숨긴 게시물입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
	})
	@GetMapping("/{postId}/likes")
	public ResponseEntity<ResultResponse> getMembersLikedPost(
		@PathVariable Long postId, @RequestParam int page, @RequestParam int size) {
		page = (page == BASE_PAGE_NUMBER ? BASE_PAGE_NUMBER : page - PAGE_ADJUSTMENT_VALUE);
		final Page<LikeMemberDto> response = postService.getPostLikeMembersDtoPage(postId, page, size);

		return ResponseEntity.ok(ResultResponse.of(GET_POST_LIKES_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 북마크")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F008 - 게시물 북마크에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F006 - 이미 해당 게시물을 저장하였습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@PostMapping("/save")
	public ResponseEntity<ResultResponse> bookmarkPost(@RequestParam Long postId) {
		postService.bookmark(postId);

		return ResponseEntity.ok(ResultResponse.of(BOOKMARK_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 북마크 해제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F009 - 게시물 북마크 해제에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F007 - 아직 해당 게시물을 저장하지 않았습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping("/save")
	public ResponseEntity<ResultResponse> unBookmarkPost(@RequestParam Long postId) {
		postService.unBookmark(postId);

		return ResponseEntity.ok(ResultResponse.of(UN_BOOKMARK_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 DM 공유")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F018 - 게시물 DM 공유에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F001 - 존재하지 않는 게시물입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "usernames", value = "공유할 회원 usernames", required = true)
	})
	@PostMapping("/share")
	public ResponseEntity<ResultResponse> sharePost(@RequestParam Long postId, @RequestParam List<String> usernames) {
		postService.sharePost(postId, usernames);

		return ResponseEntity.ok(ResultResponse.of(SHARE_POST_SUCCESS));
	}

	@ApiOperation(value = "해시태그 게시물 목록 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F019 - 해시태그 게시물 목록 페이징 조회 성공"),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "page", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "size", example = "10", required = true),
		@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	})
	@GetMapping("/hashtags")
	public ResponseEntity<ResultResponse> getHashtagPosts(@RequestParam int page, @RequestParam int size,
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		page = (page == BASE_PAGE_NUMBER ? BASE_PAGE_NUMBER : page - PAGE_ADJUSTMENT_VALUE);
		final Page<PostDto> response = postService.getHashTagPosts(page, size, hashtag.substring(HASHTAG_PREFIX_LENGTH));

		return ResponseEntity.ok(ResultResponse.of(GET_HASHTAG_POSTS_SUCCESS, response));
	}

}
