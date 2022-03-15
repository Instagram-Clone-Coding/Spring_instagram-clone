package cloneproject.Instagram.controller;

import cloneproject.Instagram.dto.StatusResponse;
import cloneproject.Instagram.dto.comment.CommentCreateRequest;
import cloneproject.Instagram.dto.comment.CommentCreateResponse;
import cloneproject.Instagram.dto.comment.CommentDTO;
import cloneproject.Instagram.dto.member.LikeMembersDTO;
import cloneproject.Instagram.dto.post.*;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

import java.util.List;

import static cloneproject.Instagram.dto.result.ResultCode.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api(tags = "게시물 API")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "게시물 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/posts")
    public ResponseEntity<ResultResponse> createPost(@ModelAttribute PostUploadRequest request) {
        final Long postId = postService.upload(request.getContent(), request.getPostImages(), request.getAltTexts(), request.getPostImageTags(), request.isCommentFlag());
        final PostCreateResponse response = new PostCreateResponse(postId);

        return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 페이징 조회(무한스크롤)")
    @ApiImplicitParam(name = "page", value = "게시물 page", example = "1", required = true)
    @GetMapping("/posts")
    public ResponseEntity<ResultResponse> getPostPage(@NotNull(message = "조회할 게시물 page는 필수입니다.") @RequestParam int page) {
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
    public ResponseEntity<ResultResponse> deletePost(@NotNull(message = "삭제할 게시물 PK는 필수입니다.") @RequestParam Long postId) {
        postService.delete(postId);

        return ResponseEntity.ok(ResultResponse.of(DELETE_POST_SUCCESS, null));
    }

    @ApiOperation(value = "게시물 조회")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ResultResponse> getPost(@NotNull(message = "조회할 게시물 PK는 필수입니다.") @PathVariable Long postId) {
        final PostResponse response = postService.getPost(postId);

        return ResponseEntity.ok(ResultResponse.of(FIND_POST_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 좋아요")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @PostMapping("/posts/like")
    public ResponseEntity<ResultResponse> likePost(@NotNull(message = "좋아요할 게시물 PK는 필수입니다.") @RequestParam Long postId) {
        final boolean status = postService.likePost(postId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(LIKE_POST_SUCCESS, response));
    }

    @ApiOperation(value = "댓글 좋아요")
    @ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
    @PostMapping("/comments/like")
    public ResponseEntity<ResultResponse> likeComment(@NotNull(message = "좋아요할 댓글 PK는 필수입니다.") @RequestParam Long commentId) {
        final boolean status = postService.likeComment(commentId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(LIKE_COMMENT_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 좋아요 취소")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @DeleteMapping("/posts/like")
    public ResponseEntity<ResultResponse> unlikePost(@NotNull(message = "좋아요 취소할 게시물 PK는 필수입니다.") @RequestParam Long postId){
        final boolean status = postService.unlikePost(postId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(UNLIKE_POST_SUCCESS, response));
    }

    @ApiOperation(value = "댓글 좋아요 취소")
    @ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
    @DeleteMapping("/comments/like")
    public ResponseEntity<ResultResponse> unlikeComment(@NotNull(message = "좋아요 취소할 댓글 PK는 필수입니다.") @RequestParam Long commentId){
        final boolean status = postService.unlikeComment(commentId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(UNLIKE_COMMENT_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 좋아요한 사람 목록 페이징 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
            @ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
            @ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
    })
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<ResultResponse> getPostLikes(
            @NotNull(message = "게시물 좋아요한 사람 목록 조회할 게시물 PK는 필수입니다.") @PathVariable Long postId,
            @NotNull(message = "page는 필수입니다.") @RequestParam int page,
            @NotNull(message = "size는 필수입니다.") @RequestParam int size) {
        final Page<LikeMembersDTO> response = postService.getPostLikeMembersDtoPage(postId, page, size);

        return ResponseEntity.ok(ResultResponse.of(GET_POST_LIKES_SUCCESS, response));
    }

    @ApiOperation(value = "댓글 좋아요한 사람 목록 페이징 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true),
            @ApiImplicitParam(name = "page", value = "페이지", example = "1", required = true),
            @ApiImplicitParam(name = "size", value = "페이지당 개수", example = "10", required = true)
    })
    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<ResultResponse> getCommentLikes(
            @NotNull(message = "댓글 좋아요한 사람 목록 조회할 게시물 PK는 필수입니다.") @PathVariable Long commentId,
            @NotNull(message = "page는 필수입니다.") @RequestParam int page,
            @NotNull(message = "size는 필수입니다.") @RequestParam int size) {
        final Page<LikeMembersDTO> response = postService.getCommentLikeMembersDtoPage(commentId, page, size);

        return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_LIKES_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 저장")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @PostMapping("/posts/save")
    public ResponseEntity<ResultResponse> savePost(@NotNull(message = "저장할 게시물 PK는 필수입니다.") @RequestParam Long postId) {
        final boolean status = postService.savePost(postId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(SAVE_POST_SUCCESS, response)); // TODO: status_response 제거
    }

    @ApiOperation(value = "게시물 저장 취소")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @DeleteMapping("/posts/save")
    public ResponseEntity<ResultResponse> unsavePost(@NotNull(message = "저장 취소할 게시물 PK는 필수입니다.") @RequestParam Long postId) {
        final boolean status = postService.unsavePost(postId);
        final StatusResponse response = new StatusResponse(status);

        return ResponseEntity.ok(ResultResponse.of(UNSAVE_POST_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 댓글 작성", notes = "parentId는 댓글인 경우 0, 답글인 경우 댓글 부모 PK를 입력해주세요.")
    @PostMapping("/comments")
    public ResponseEntity<ResultResponse> createComment(@RequestBody CommentCreateRequest request) {
        final CommentCreateResponse response = postService.createComment(request);

        return ResponseEntity.ok(ResultResponse.of(CREATE_COMMENT_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 댓글 삭제")
    @ApiImplicitParam(name = "commentId", value = "댓글 PK", example = "1", required = true)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ResultResponse> deleteComment(@NotNull(message = "댓글 PK는 필수입니다.") @PathVariable Long commentId) {
        final StatusResponse response = postService.deleteComment(commentId);

        return ResponseEntity.ok(ResultResponse.of(DELETE_COMMENT_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 댓글 페이징 조회",
            notes = "게시물 조회에서 최근 댓글 10개를 응답하므로, 2페이지부터 조회해주세요." +
                    "조회 중간에 새 댓글이 추가되면, 추가 조회 시 중복 데이터가 발생할 수 있으므로, 중복 데이터는 걸러서 뷰에 표현해주시면 됩니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
            @ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
    })
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ResultResponse> getCommentPage(
            @NotNull(message = "게시물 PK는 필수입니다.") @PathVariable Long postId,
            @NotNull(message = "조회할 댓글 page는 필수입니다.") @RequestParam int page) {
        final Page<CommentDTO> response = postService.getCommentDtoPage(postId, page);

        return ResponseEntity.ok(ResultResponse.of(GET_COMMENT_PAGE_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 답글 페이징 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "부모 댓글 PK", example = "1", required = true),
            @ApiImplicitParam(name = "page", value = "댓글 page", example = "1", required = true)
    })
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ResultResponse> getReplyPage(
            @NotNull(message = "부모 댓글 PK는 필수입니다.") @PathVariable Long commentId,
            @NotNull(message = "조회할 댓글 page는 필수입니다.") @RequestParam int page) {
        final Page<CommentDTO> response = postService.getReplyDtoPage(commentId, page);

        return ResponseEntity.ok(ResultResponse.of(GET_REPLY_PAGE_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 DM 공유")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true),
            @ApiImplicitParam(name = "usernames", value = "공유할 회원 usernames", required = true)
    })
    @PostMapping("/posts/share")
    public ResponseEntity<ResultResponse> sharePost(
            @NotNull(message = "공유할 게시물 PK는 필수입니다.") @RequestParam Long postId,
            @RequestParam List<String> usernames) {
        final StatusResponse response = postService.sharePost(postId, usernames);

        return ResponseEntity.ok(ResultResponse.of(SHARE_POST_SUCCESS, response));
    }
}
