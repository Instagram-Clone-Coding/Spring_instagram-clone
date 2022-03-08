package cloneproject.Instagram.controller;

import cloneproject.Instagram.dto.post.PostCreateResponse;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.dto.story.StoryUploadRequest;
import cloneproject.Instagram.service.StoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static cloneproject.Instagram.dto.result.ResultCode.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api(tags = "채팅 API")
@Validated
@RestController
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @ApiOperation(value = "스토리 업로드", consumes = MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/stories")
    public ResponseEntity<ResultResponse> createStory(@ModelAttribute StoryUploadRequest request) {
        final Long postId = storyService.upload(request);
        final PostCreateResponse response = new PostCreateResponse(postId);

        return ResponseEntity.ok(ResultResponse.of(CREATE_STORY_SUCCESS, response));
    }
}
