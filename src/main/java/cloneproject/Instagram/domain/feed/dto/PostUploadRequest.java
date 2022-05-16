package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUploadRequest {

	@ApiModelProperty(value = "게시물 내용", example = "안녕하세요!", required = true)
	@Length(max = 2200, message = "최대 2,200자까지 입력 가능합니다.")
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
	private boolean commentFlag;

}
