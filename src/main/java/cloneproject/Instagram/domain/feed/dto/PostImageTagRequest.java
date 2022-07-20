package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageTagRequest {

	@ApiModelProperty(value = "게시물 이미지 순번", example = "1", required = true)
	@NotNull(message = "게시물 이미지 순번은 필수입니다.")
	private Long id;

	@ApiModelProperty(value = "게시물 이미지 태그 x 좌표", example = "50", required = true)
	@NotNull(message = "게시물 이미지 태그 x 좌표는 필수입니다.")
	@Min(value = 0, message = "x 좌표는 0 ~ 100 사이로 입력해주세요.")
	@Max(value = 100, message = "x 좌표는 0 ~ 100 사이로 입력해주세요.")
	private Double tagX;

	@ApiModelProperty(value = "게시물 이미지 태그 y 좌표", example = "50", required = true)
	@NotNull(message = "게시물 이미지 태그 y 좌표는 필수입니다.")
	@Min(value = 0, message = "y 좌표는 0 ~ 100 사이로 입력해주세요.")
	@Max(value = 100, message = "y 좌표는 0 ~ 100 사이로 입력해주세요.")
	private Double tagY;

	@ApiModelProperty(value = "게시물 이미지 태그 사용자 아이디", example = "dlwlrma", required = true)
	@NotBlank(message = "게시물 이미지 태그 사용자 아이디는 필수입니다.")
	private String username;

	public void setId(Long id) {
		this.id = id;
	}

}
