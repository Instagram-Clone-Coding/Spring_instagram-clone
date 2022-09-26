package cloneproject.Instagram.domain.feed.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostUploadRequest {

	@ApiModelProperty(value = "게시물 내용", example = "안녕하세요!", required = true)
	@Size(max = 2200, message = "게시물 내용은 최대 2,200자까지 입력 가능합니다.")
	private String content;

	@ApiModelProperty(value = "게시물 이미지", required = true)
	@Size(min = 1, max = 10, message = "게시물 이미지는 1개 이상, 10개 이하만 추가할 수 있습니다.")
	private List<MultipartFile> postImages = new ArrayList<>();

	@ApiModelProperty(value = "게시물 이미지 대체 텍스트", required = true, example = "image")
	@Size(min = 1, message = "게시물 이미지 대체 텍스트는 필수입니다.")
	private List<@NotBlank(message = "게시물 이미지 대체 텍스트는 필수입니다.") String> altTexts;

	@ApiModelProperty(value = "게시물 이미지 사용자 태그")
	@Valid
	private List<PostImageTagRequest> postImageTags = new ArrayList<>();

	@ApiModelProperty(value = "댓글 기능 여부", required = true, example = "댓글 기능 사용(true) | 미사용(false)")
	@NotNull(message = "댓글 기능 여부는 필수입니다.")
	private boolean commentFlag;

	@ApiModelProperty(value = "좋아요 공개 여부", required = true, example = "좋아요 공개(true) | 비공개(false)")
	@NotNull(message = "좋아요 공개 여부는 필수입니다.")
	private boolean likeFlag;

}
