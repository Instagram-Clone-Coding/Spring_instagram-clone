package cloneproject.Instagram.dto.post;

import cloneproject.Instagram.vo.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostTagDTO {

    @JsonIgnore
    private Long postImageId;
    private Long id;
    private Tag tag;

    @QueryProjection
    public PostTagDTO(Long postImageId, Long id, Tag tag) {
        this.postImageId = postImageId;
        this.id = id;
        this.tag = tag;
    }
}
