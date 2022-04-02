package cloneproject.Instagram.domain.feed.controller;

import cloneproject.Instagram.domain.feed.dto.PostUploadResponse;
import cloneproject.Instagram.domain.feed.dto.PostDTO;
import cloneproject.Instagram.domain.feed.dto.PostResponse;
import cloneproject.Instagram.domain.feed.dto.PostUploadRequest;
import cloneproject.Instagram.domain.feed.service.PostService;
import cloneproject.Instagram.domain.member.dto.LikeMembersDTO;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cloneproject.Instagram.global.result.ResultCode.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Api(tags = "게시물 API")
@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@ApiOperation(value = "게시물 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
	@PostMapping("/posts")
	public ResponseEntity<ResultResponse> uploadPost(@Validated @ModelAttribute PostUploadRequest request) {
		final PostUploadResponse response = postService.upload(request);

		return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 목록 페이징 조회")
	@ApiImplicitParam(name = "page", value = "게시물 page", example = "1", required = true)
	@GetMapping("/posts")
	public ResponseEntity<ResultResponse> getPostPage(@RequestParam int page) {
		final Page<PostDTO> postPage = postService.getPostDtoPage(1, page);

		return ResponseEntity.ok(ResultResponse.of(FIND_POST_PAGE_SUCCESS, postPage));
	}

	@ApiOperation(value = "최근 게시물 10개 조회")
	@GetMapping("/posts/recent")
	public ResponseEntity<ResultResponse> getRecent10Posts() {
		final List<PostDTO> postList = postService.getRecent10PostDTOs();

		return ResponseEntity.ok(ResultResponse.of(FIND_RECENT10POSTS_SUCCESS, postList));
	}

	@ApiOperation(value = "게시물 삭제")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping("/posts")
	public ResponseEntity<ResultResponse> deletePost(@RequestParam Long postId) {
		postService.delete(postId);

		return ResponseEntity.ok(ResultResponse.of(DELETE_POST_SUCCESS, null));
	}

	@ApiOperation(value = "게시물 조회")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@GetMapping("/posts/{postId}")
	public ResponseEntity<ResultResponse> getPost(@PathVariable Long postId) {
		final PostResponse response = postService.getPostResponse(postId);

		return ResponseEntity.ok(ResultResponse.of(FIND_POST_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 좋아요")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@PostMapping("/posts/like")
	public ResponseEntity<ResultResponse> likePost(@RequestParam Long postId) {
		postService.likePost(postId);

		return ResponseEntity.ok(ResultResponse.of(LIKE_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 좋아요 해제")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping("/posts/like")
	public ResponseEntity<ResultResponse> unlikePost(@RequestParam Long postId) {
		postService.unlikePost(postId);

		return ResponseEntity.ok(ResultResponse.of(UN_LIKE_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 좋아요한 회원 목록 페이징 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
	})
	@GetMapping("/posts/{postId}/likes")
	public ResponseEntity<ResultResponse> getMembersLikedPost(
		@PathVariable Long postId, @RequestParam int page, @RequestParam int size) {
		final Page<LikeMembersDTO> response = postService.getPostLikeMembersDtoPage(postId, page, size);

		return ResponseEntity.ok(ResultResponse.of(GET_POST_LIKES_SUCCESS, response));
	}

	@ApiOperation(value = "게시물 북마크")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@PostMapping("/posts/save")
	public ResponseEntity<ResultResponse> bookmarkPost(@RequestParam Long postId) {
		postService.bookmark(postId);

		return ResponseEntity.ok(ResultResponse.of(BOOKMARK_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 북마크 해제")
	@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
	@DeleteMapping("/posts/save")
	public ResponseEntity<ResultResponse> unBookmarkPost(@RequestParam Long postId) {
		postService.unBookmark(postId);

		return ResponseEntity.ok(ResultResponse.of(UN_BOOKMARK_POST_SUCCESS));
	}

	@ApiOperation(value = "게시물 DM 공유")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "usernames", value = "공유할 회원 usernames", required = true)
	})
	@PostMapping("/posts/share")
	public ResponseEntity<ResultResponse> sharePost(
		@RequestParam Long postId, @RequestParam List<String> usernames) {
		postService.sharePost(postId, usernames);

		return ResponseEntity.ok(ResultResponse.of(SHARE_POST_SUCCESS));
	}

	@ApiOperation(value = "해시태그 게시물 목록 페이징 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "page", value = "page", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "size", example = "10", required = true),
		@ApiImplicitParam(name = "hashtag", value = "hashtag", example = "#만두", required = true)
	})
	@GetMapping("/hashtags/posts")
	public ResponseEntity<ResultResponse> getHashtagPosts(
		@NotNull(message = "page는 필수입니다.") @RequestParam int page,
		@NotNull(message = "size는 필수입니다.") @RequestParam int size,
		@NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
		final Page<PostDTO> response = postService.getHashTagPosts(page, size, hashtag.substring(1));

		return ResponseEntity.ok(ResultResponse.of(GET_HASHTAG_POSTS_SUCCESS, response));
	}
}
