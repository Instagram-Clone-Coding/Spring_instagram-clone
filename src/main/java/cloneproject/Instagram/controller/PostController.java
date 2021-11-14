package cloneproject.Instagram.controller;

import cloneproject.Instagram.config.CustomValidator;
import cloneproject.Instagram.dto.post.PostCreateResponse;
import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.dto.post.PostImageUploadResponse;
import cloneproject.Instagram.dto.result.ResultResponse;
import cloneproject.Instagram.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

import java.util.List;

import static cloneproject.Instagram.dto.result.ResultCode.*;

@Api(tags = "게시물 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CustomValidator validator;

    @ApiImplicitParam(name = "content", value = "게시물 내용", example = "안녕하세요.", required = true)
    @PostMapping("/posts")
    public ResponseEntity<ResultResponse> createPost(
            @Validated @Length(max = 2200, message = "최대 2,200자까지 입력 가능합니다.")
            @RequestParam String content) {
        final Long postId = postService.create(content);
        PostCreateResponse response = new PostCreateResponse(postId);

        return ResponseEntity.ok(ResultResponse.of(CREATE_POST_SUCCESS, response));
    }

    @ApiImplicitParam(name = "id", value = "게시물 PK", example = "1", required = true)
    @PostMapping("/posts/images")
    public ResponseEntity<ResultResponse> uploadImages(
            @Validated @NotNull(message = "게시물 PK는 필수입니다.")
            @RequestParam Long id,
            @RequestParam MultipartFile[] uploadImages) {
        final List<Long> imageIdList = postService.uploadImages(id, uploadImages);
        PostImageUploadResponse response = new PostImageUploadResponse(imageIdList);

        return ResponseEntity.ok(ResultResponse.of(UPLOAD_IMAGES_SUCCESS, response));
    }

    @PostMapping("/posts/images/tags")
    public ResponseEntity<ResultResponse> uploadImageTags(@RequestBody List<PostImageTagRequest> requests, BindingResult bindingResult) throws BindException {
        validator.validate(requests, bindingResult);
        if (bindingResult.hasErrors())
            throw new BindException(bindingResult);

        postService.addTags(requests);

        return ResponseEntity.ok(ResultResponse.of(ADD_TAGS_SUCCESS, null));
    }
}
