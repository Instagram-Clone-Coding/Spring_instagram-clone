package cloneproject.Instagram.dto.story;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoryContentRequest {

    @ApiModelProperty(value = "스토리 이미지 순번", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(value = "스토리 텍스트 내용", example = "This is a story @dlwlrma", required = true)
    private String content;
}
