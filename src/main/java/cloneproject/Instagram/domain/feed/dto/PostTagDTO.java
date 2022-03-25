package cloneproject.Instagram.domain.feed.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.feed.vo.Tag;
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
