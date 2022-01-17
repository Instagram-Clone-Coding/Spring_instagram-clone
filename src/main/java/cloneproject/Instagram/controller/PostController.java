package cloneproject.Instagram.controller;

import cloneproject.Instagram.dto.post.*;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "게시물 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/posts")
    public ResponseEntity<ResultResponse> createPost(@Validated @ModelAttribute PostUploadRequest request) {
        final Long postId = postService.upload(request.getContent(), request.getPostImages(), request.getPostImageTags());
        final PostCreateResponse response = new PostCreateResponse(postId);

        return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
    }

    @ApiOperation(value = "게시물 페이징 조회(무한스크롤)")
    @ApiImplicitParam(name = "page", value = "게시물 page", example = "1", required = true)
    @GetMapping("/posts")
    public ResponseEntity<ResultResponse> getPostPage(
            @Validated @NotNull(message = "조회할 게시물 page는 필수입니다.") @RequestParam int page) {
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
    public ResponseEntity<ResultResponse> deletePost(
            @Validated @NotNull(message = "삭제할 게시물 PK는 필수입니다.") @RequestParam Long postId) {
        postService.delete(postId);

        return ResponseEntity.ok(ResultResponse.of(DELETE_POST_SUCCESS, null));
    }

    @ApiOperation(value = "게시물 조회")
    @ApiImplicitParam(name = "postId", value = "게시물 PK", example = "1", required = true)
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ResultResponse> getPost(
            @Validated @NotNull(message = "조회할 게시물 PK는 필수입니다.") @PathVariable Long postId) {
        final PostResponse response = postService.getPost(postId);

        return ResponseEntity.ok(ResultResponse.of(FIND_POST_SUCCESS, response));
    }
}
