package cloneproject.Instagram.dto.story;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryUploadRequest {

    @ApiModelProperty(value = "스토리 텍스트", required = true)
    private List<StoryContentRequest> storyContents = new ArrayList<>();

    @ApiModelProperty(value = "스토리 이미지", required = true)
    private List<MultipartFile> storyImages = new ArrayList<>();
}
