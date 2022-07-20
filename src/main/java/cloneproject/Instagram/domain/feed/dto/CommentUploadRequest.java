package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "게시물 댓글 작성 요청 데이터 모델")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUploadRequest {

	@ApiModelProperty(value = "게시물 PK", example = "1", required = true)
	@NotNull(message = "게시물 PK는 필수입니다.")
	private Long postId;

	@ApiModelProperty(value = "댓글 부모 PK", example = "0", required = true)
	@NotNull(message = "부모 댓글 PK는 필수입니다.")
	private Long parentId;

	@ApiModelProperty(value = "댓글 내용", example = "댓글", required = true)
	@NotBlank(message = "댓글 내용은 필수입니다.")
	@Length(max = 100, message = "최대 100자까지 입력 가능합니다.")
	private String content;

}
