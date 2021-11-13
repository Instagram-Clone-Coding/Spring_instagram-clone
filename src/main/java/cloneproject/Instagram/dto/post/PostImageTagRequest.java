package cloneproject.Instagram.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImageTagRequest {

    @ApiModelProperty(value = "게시물 이미지 PK", example = "3", required = true)
    @NotNull(message = "게시물 이미지 PK는 필수입니다.")
    private Long id;

    @NotEmpty(message = "게시물 이미지 태그는 필수입니다.")
    private List<PostImageTagDTO> postImageTagDTOs;
}