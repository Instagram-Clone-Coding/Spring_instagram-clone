package cloneproject.Instagram.controller;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.result.ResultCode;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.HashtagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static cloneproject.Instagram.dto.result.ResultCode.*;

@Api(tags = "해시태그 API")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @ApiOperation(value = "해시태그 게시물 목록 페이징 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", example = "1", required = true),
            @ApiImplicitParam(name = "size", value = "size", example = "10", required = true),
            @ApiImplicitParam(name = "hashtag", value = "hashtag", example = "만두", required = true)
    })
    @GetMapping("/hashtags/posts")
    public ResponseEntity<ResultResponse> getHashtagPosts(
            @NotNull(message = "page는 필수입니다.") @RequestParam int page,
            @NotNull(message = "size는 필수입니다.") @RequestParam int size,
            @NotBlank(message = "hashtag는 필수입니다.") @RequestParam String hashtag) {
        final Page<PostDTO> response = hashtagService.getHashTagPosts(page, size, hashtag);

        return ResponseEntity.ok(ResultResponse.of(GET_HASHTAG_POSTS_SUCCESS, response));
    }
}
