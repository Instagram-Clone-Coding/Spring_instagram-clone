package cloneproject.Instagram.domain.feed.controller;

import cloneproject.Instagram.domain.feed.dto.CommentUploadRequest;
import cloneproject.Instagram.domain.feed.dto.CommentUploadResponse;
import cloneproject.Instagram.domain.feed.dto.CommentDto;
import cloneproject.Instagram.domain.feed.service.CommentService;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static cloneproject.Instagram.global.result.ResultCode.*;

import javax.validation.Valid;

@Api(tags = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;

	@ApiOperation(value = "댓글 업로드", notes = "parentId는 댓글인 경우 0, 답글인 경우 댓글 부모 PK를 입력해 주세요.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F010 - 댓글 업로드에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G005 - request message body가 없거나, 값 타입이 올바르지 않습니다."
			+ "F001 - 존재하지 않는 게시물입니다.\n"
			+ "F012 - 댓글 기능이 해제된 게시물에는 댓글을 작성할 수 없습니다.\n"
			+ "F013 - 최상위 댓글에만 답글을 업로드할 수 있습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@PostMapping
	public ResponseEntity<ResultResponse> createComment(@Valid @RequestBody CommentUploadRequest request) {
		final CommentUploadResponse response = commentService.uploadComment(request);

		return ResponseEntity.ok(ResultResponse.of(CREATE_COMMENT_SUCCESS, response));
	}

	@ApiOperation(value = "댓글 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F011 - 댓글 삭제에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F008 - 존재하지 않는 댓글입니다.\n"
			+ "F009 - 타인이 작성한 댓글은 삭제할 수 없습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@DeleteMapping("/{commentId}")
	public ResponseEntity<ResultResponse> deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(DELETE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 페이징 조회",
		notes = "게시물 조회에서 최근 댓글 10개를 응답하므로, <b>2페이지부터 조회</b>해 주세요.<br>" +
			"조회 중간에 새 댓글이 추가되면, 추가 조회 시 중복 데이터가 발생할 수 있으므로, " +
			"중복 데이터는 걸러서 뷰에 표현해 주시면 됩니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F012 - 댓글 목록 페이지 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
	})
	@GetMapping("/posts/{postId}")
	public ResponseEntity<ResultResponse> getCommentPage(@PathVariable Long postId, @RequestParam int page) {
		final Page<CommentDto> response = commentService.getCommentDtoPage(postId, page);

		return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_PAGE_SUCCESS, response));
	}

	@ApiOperation(value = "답글 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F013 - 답글 목록 페이지 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "commentId", value = "부모 댓글 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
	})
	@GetMapping("/{commentId}")
	public ResponseEntity<ResultResponse> getReplyPage(@PathVariable Long commentId, @RequestParam int page) {
		final Page<CommentDto> response = commentService.getReplyDtoPage(commentId, page);

		return ResponseEntity.ok(ResultResponse.of(GET_REPLY_PAGE_SUCCESS, response));
	}

	@ApiOperation(value = "댓글 좋아요")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F015 - 댓글 좋아요에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F008 - 존재하지 않는 댓글입니다.\n"
			+ "F010 - 해당 댓글에 이미 좋아요를 누른 회원입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@PostMapping("/like")
	public ResponseEntity<ResultResponse> likeComment(@RequestParam Long commentId) {
		commentService.likeComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(LIKE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 좋아요 취소")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F016 - 댓글 좋아요 해제에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다.\n"
			+ "F008 - 존재하지 않는 댓글입니다.\n"
			+ "F011 - 해당 댓글에 좋아요를 누르지 않은 회원입니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
	@DeleteMapping("/like")
	public ResponseEntity<ResultResponse> unlikeComment(@RequestParam Long commentId) {
		commentService.unlikeComment(commentId);

		return ResponseEntity.ok(ResultResponse.of(UNLIKE_COMMENT_SUCCESS));
	}

	@ApiOperation(value = "댓글 좋아요한 회원 목록 페이징 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "F017 - 댓글에 좋아요한 회원 목록 페이지 조회에 성공하였습니다."),
		@ApiResponse(code = 400, message = "G003 - 유효하지 않은 입력입니다.\n"
			+ "G004 - 입력 타입이 유효하지 않습니다."),
		@ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
	})
	@ApiImplicitParams({
		@ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true),
		@ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
		@ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
	})
	@GetMapping("/{commentId}/likes")
	public ResponseEntity<ResultResponse> getCommentLikes(@PathVariable Long commentId, @RequestParam int page,
		@RequestParam int size) {
		final Page<LikeMemberDto> response = commentService.getCommentLikeMembersDtoPage(commentId, page, size);

		return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_LIKES_SUCCESS, response));
	}

}
