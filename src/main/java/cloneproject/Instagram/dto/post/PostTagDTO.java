package cloneproject.Instagram.dto.post;

import cloneproject.Instagram.vo.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostTagDTO {

    private Long id;
    private Tag tag;
}
