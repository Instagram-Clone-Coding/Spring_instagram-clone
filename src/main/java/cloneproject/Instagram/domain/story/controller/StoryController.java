package cloneproject.Instagram.domain.story.controller;

import cloneproject.Instagram.domain.story.dto.MemberStoryDto;
import cloneproject.Instagram.domain.story.dto.StoryUploadRequest;
import cloneproject.Instagram.domain.story.service.StoryService;
import cloneproject.Instagram.global.dto.StatusResponse;
import cloneproject.Instagram.global.result.ResultResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cloneproject.Instagram.global.result.ResultCode.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api(tags = "스토리 API")
@Validated
@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @ApiOperation(value = "스토리 업로드")
    @PostMapping(value = "/stories", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createStory(@ModelAttribute StoryUploadRequest request) {
        final StatusResponse response = storyService.upload(request);

        return ResponseEntity.ok(ResultResponse.of(CREATE_STORY_SUCCESS, response));
    }

    @ApiOperation(value = "회원 스토리 조회")
    @ApiImplicitParam(name = "memberId", value = "회원 PK", example = "1", required = true)
    @GetMapping(value = "/members/{memberId}/stories")
    public ResponseEntity<ResultResponse> getStory(@PathVariable Long memberId) {
        final MemberStoryDto response = storyService.getMemberStories(memberId);

        return ResponseEntity.ok(ResultResponse.of(GET_STORY_SUCCESS, response));
    }
}
