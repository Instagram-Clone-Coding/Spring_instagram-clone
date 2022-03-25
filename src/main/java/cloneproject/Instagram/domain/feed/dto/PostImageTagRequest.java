package cloneproject.Instagram.domain.feed.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageTagRequest {

    @ApiModelProperty(value = "게시물 이미지 순번", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "게시물 이미지 태그 x좌표", example = "50", required = true)
    private Long tagX;

    @ApiModelProperty(value = "게시물 이미지 태그 y좌표", example = "50", required = true)
    private Long tagY;

    @ApiModelProperty(value = "게시물 이미지 태그 사용자 아이디", example = "dlwlrma", required = true)
    private String username;
}