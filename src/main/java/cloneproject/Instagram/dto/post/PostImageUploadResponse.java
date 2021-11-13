package cloneproject.Instagram.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@ApiModel("게시물 이미지 업로드 응답 데이터 모델")
@Getter
@AllArgsConstructor
public class PostImageUploadResponse {

    @ApiModelProperty(value = "게시물 이미지 PK 리스트", example = "{3, 4, 5}")
    List<Long> imageIds;
}
