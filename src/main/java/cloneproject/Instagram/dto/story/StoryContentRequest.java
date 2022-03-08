package cloneproject.Instagram.dto.story;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoryContentRequest {

    @ApiModelProperty(value = "스토리 이미지 순번", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "스토리 텍스트 x좌표", example = "50", required = true)
    private Long x;

    @ApiModelProperty(value = "스토리 텍스트 y좌표", example = "50", required = true)
    private Long y;

    @ApiModelProperty(value = "스토리 텍스트 내용", example = "This is a story @dlwlrma", required = true)
    private String content;
}
