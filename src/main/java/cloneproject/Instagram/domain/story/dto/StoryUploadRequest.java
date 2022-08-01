package cloneproject.Instagram.domain.story.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryUploadRequest {

	@ApiModelProperty(value = "스토리 텍스트", required = true)
	private List<StoryContentRequest> storyContents = new ArrayList<>();

	@ApiModelProperty(value = "스토리 이미지", required = true)
	private List<MultipartFile> storyImages = new ArrayList<>();

}
