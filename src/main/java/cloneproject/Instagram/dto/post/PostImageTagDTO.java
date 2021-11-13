package cloneproject.Instagram.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageTagDTO {

    @ApiModelProperty(value = "게시물 이미지 태그 x좌표", example = "50", required = true)
    private Long tagX;

    @ApiModelProperty(value = "게시물 이미지 태그 y좌표", example = "50", required = true)
    private Long tagY;

    @ApiModelProperty(value = "게시물 이미지 태그 사용자 아이디", example = "dlwlrma", required = true)
    private String username;
}