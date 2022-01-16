package cloneproject.Instagram.dto.post;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

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
    private List<MultipartFile> postImages = new ArrayList<>();

    @ApiModelProperty(value = "게시물 이미지 사용자 태그")
    private List<PostImageTagRequest> postImageTags  = new ArrayList<>();
}
