package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ApiModel("게시물 생성 응답 데이터 모델")
@Getter
@AllArgsConstructor
public class PostUploadResponse {

	@ApiModelProperty(value = "게시물 PK", example = "1")
	private Long postId;
}
