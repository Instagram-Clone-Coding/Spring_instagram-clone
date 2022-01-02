package cloneproject.Instagram.dto.post;

import cloneproject.Instagram.vo.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostImageDTO {

    private Long id;
    private Image image;
    private List<PostTagDTO> postTagDTOs = new ArrayList<>();
}
