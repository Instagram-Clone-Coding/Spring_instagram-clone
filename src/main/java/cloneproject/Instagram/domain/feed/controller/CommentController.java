package cloneproject.Instagram.domain.feed.controller;

import cloneproject.Instagram.domain.feed.dto.CommentUploadRequest;
import cloneproject.Instagram.domain.feed.dto.CommentUploadResponse;
import cloneproject.Instagram.domain.feed.dto.CommentDTO;
import cloneproject.Instagram.domain.feed.service.CommentService;
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

import static cloneproject.Instagram.global.result.ResultCode.*;

@Api(tags = "댓글 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@ApiOperation(value = "댓글 업로드", notes = "parentId는 댓글인 경우 0, 답글인 경우 댓글 부모 PK를 입력해 주세요.")
	@PostMapping("/comments")
	public ResponseEntity<ResultResponse> createComment(@Validated @RequestBody CommentUploadRequest request) {
		final CommentUploadResponse response = commentService.uploadComment(request);

		return ResponseEntity.ok(ResultResponse.of(CREATE_COMMENT_SUCCESS, response));
	}

	@ApiOperation(value = "댓글 삭제")
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ResultResponse> deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(DELETE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 페이징 조회",
		notes = "게시물 조회에서 최근 댓글 10개를 응답하므로, <b>2페이지부터 조회</b>해 주세요.<br>" +
			"조회 중간에 새 댓글이 추가되면, 추가 조회 시 중복 데이터가 발생할 수 있으므로, " +
			"중복 데이터는 걸러서 뷰에 표현해 주시면 됩니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
	})
	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<ResultResponse> getCommentPage(@PathVariable Long postId, @RequestParam int page) {
		final Page<CommentDTO> response = commentService.getCommentDtoPage(postId, page);

		return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_PAGE_SUCCESS, response));
	}

	@ApiOperation(value = "답글 페이징 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "commentId", value = "부모 댓글 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
	})
	@GetMapping("/comments/{commentId}")
	public ResponseEntity<ResultResponse> getReplyPage(@PathVariable Long commentId, @RequestParam int page) {
		final Page<CommentDTO> response = commentService.getReplyDtoPage(commentId, page);

		return ResponseEntity.ok(ResultResponse.of(GET_REPLY_PAGE_SUCCESS, response));
	}

	@ApiOperation(value = "댓글 좋아요")
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@PostMapping("/comments/like")
	public ResponseEntity<ResultResponse> likeComment(@RequestParam Long commentId) {
		commentService.likeComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(LIKE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 좋아요 취소")
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@DeleteMapping("/comments/like")
	public ResponseEntity<ResultResponse> unlikeComment(@RequestParam Long commentId) {
		commentService.unlikeComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(UNLIKE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 좋아요한 회원 목록 페이징 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
	})
	@GetMapping("/comments/{commentId}/likes")
	public ResponseEntity<ResultResponse> getCommentLikes(@PathVariable Long commentId, @RequestParam int page,
		@RequestParam int size) {
		final Page<LikeMembersDTO> response = commentService.getCommentLikeMembersDtoPage(commentId, page, size);

		return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_LIKES_SUCCESS, response));
	}
}
