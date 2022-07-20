package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUploadRequest {

	@ApiModelProperty(value = "게시물 내용", example = "안녕하세요!", required = true)
	@Size(max = 2200, message = "최대 2,200자까지 입력 가능합니다.")
	private String content;

	@ApiModelProperty(value = "게시물 이미지", required = true)
	@Size(min = 1, message = "게시물 이미지는 필수입니다.")
	private List<MultipartFile> postImages = new ArrayList<>();

	@ApiModelProperty(value = "게시물 이미지 대체 텍스트", required = true)
	@Size(min = 1, message = "게시물 이미지 대체 텍스트는 필수입니다.")
	private List<@NotBlank(message = "게시물 이미지 대체 텍스트는 필수입니다.") String> altTexts;

	@ApiModelProperty(value = "게시물 이미지 사용자 태그")
	@Valid
	private List<PostImageTagRequest> postImageTags = new ArrayList<>();

	@ApiModelProperty(value = "댓글 기능 여부", required = true)
	@NotNull(message = "댓글 기능 여부는 필수입니다.")
	private boolean commentFlag;

	@ApiModelProperty(value = "좋아요 숨기기 여부", required = true)
	@NotNull(message = "좋아요 숨기기 여부는 필수입니다.")
	private boolean likeFlag;

}
