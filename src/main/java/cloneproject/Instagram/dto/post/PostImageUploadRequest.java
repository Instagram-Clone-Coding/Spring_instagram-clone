package cloneproject.Instagram.dto.post;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class PostImageUploadRequest {

    @ApiParam(value = "게시물 PK", example = "1", required = true)
    @NotNull(message = "게시물 PK는 필수입니다.")
    private Long id;

    @ApiParam(value = "게시물 이미지", required = true)
    private ArrayList<MultipartFile> uploadImages;
}
