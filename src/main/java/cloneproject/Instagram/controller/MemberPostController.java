package cloneproject.Instagram.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloneproject.Instagram.dto.post.MemberPostDTO;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.MemberPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "멤버 게시물 API")
@RestController
@RequiredArgsConstructor
public class MemberPostController {

    private final MemberPostService memberPostService;

    @ApiOperation(value = "멤버 게시물 15개 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "있어도 되고 없어도됨", required = false, example = "Bearer AAA.BBB.CCC"),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @GetMapping("/accounts/{username}/posts/recent")
    public ResponseEntity<ResultResponse> getRecent10Posts(
            @PathVariable("username") @Validated @NotBlank(message="username은 필수입니다") String username) {
        final List<MemberPostDTO> postList = memberPostService.getRecent15PostDTOs(username);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_RECENT15_MEMBER_POSTS_SUCCESS, postList));
    }

    @ApiOperation(value = "멤버 게시물 페이징 조회(무한스크롤)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "있어도 되고 없어도됨", required = false, example = "Bearer AAA.BBB.CCC"),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
        @ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
    })
    @GetMapping("/accounts/{username}/posts")
    public ResponseEntity<ResultResponse> getPostPage(
            @PathVariable("username") @Validated @NotBlank(message="username은 필수입니다") String username,
            @Validated @NotNull(message = "조회할 게시물 page는 필수입니다.") @RequestParam int page) {
        final Page<MemberPostDTO> postPage = memberPostService.getMemberPostDto(username,3, page);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_MEMBER_POSTS_SUCCESS, postPage));
    }

    // ============== 저장 ================
    @ApiOperation(value = "멤버 저장한 게시물 15개 조회")
    @GetMapping("/accounts/{username}/posts/saved/recent")
    public ResponseEntity<ResultResponse> getRecent1SavedPosts() {
        final List<MemberPostDTO> postList = memberPostService.getRecent15SavedPostDTOs();

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_RECENT15_MEMBER_SAVED_POSTS_SUCCESS, postList));
    }

    @ApiOperation(value = "멤버 저장한 게시물 페이징 조회(무한스크롤)")
    @GetMapping("/accounts/{username}/posts/saved")
    @ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
    public ResponseEntity<ResultResponse> getSavedPostPage(
            @Validated @NotNull(message = "조회할 게시물 page는 필수입니다.") @RequestParam int page) {
        final Page<MemberPostDTO> postPage = memberPostService.getMemberSavedPostDto(3, page);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_MEMBER_SAVED_POSTS_SUCCESS, postPage));
    }

    // ============== 태그 ================
    @ApiOperation(value = "멤버 태그된 게시물 15개 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "있어도 되고 없어도됨", required = false, example = "Bearer AAA.BBB.CCC"),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma")
    })
    @GetMapping("/accounts/{username}/posts/tagged/recent")
    public ResponseEntity<ResultResponse> getRecent10TaggedPosts(
            @PathVariable("username") @Validated @NotBlank(message="username은 필수입니다") String username) {
        final List<MemberPostDTO> postList = memberPostService.getRecent15TaggedPostDTOs(username);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_RECENT15_MEMBER_TAGGED_POSTS_SUCCESS, postList));
    }

    @ApiOperation(value = "멤버 태그된 게시물 페이징 조회(무한스크롤)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "있어도 되고 없어도됨", required = false, example = "Bearer AAA.BBB.CCC"),
        @ApiImplicitParam(name = "username", value = "유저네임", required = true, example = "dlwlrma"),
        @ApiImplicitParam(name = "page", value = "페이지", required = true, example = "1")
    })
    @GetMapping("/accounts/{username}/posts/tagged")
    public ResponseEntity<ResultResponse> getTaggedPostPage(
            @PathVariable("username") @Validated @NotBlank(message="username은 필수입니다") String username,
            @Validated @NotNull(message = "조회할 게시물 page는 필수입니다.") @RequestParam int page) {
        final Page<MemberPostDTO> postPage = memberPostService.getMemberTaggedPostDto(username,3, page);

        return ResponseEntity.ok(ResultResponse.of(ResultCode.FIND_MEMBER_TAGGED_POSTS_SUCCESS, postPage));
    }
}
