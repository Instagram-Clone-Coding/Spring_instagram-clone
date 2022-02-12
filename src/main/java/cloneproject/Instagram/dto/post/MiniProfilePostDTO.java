package cloneproject.Instagram.dto.post;


import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MiniProfilePostDTO {

    private Long postId;
    private String postImageUrl;

    @Builder
    @QueryProjection
    public MiniProfilePostDTO(Long postId, String postImageUrl) {
        this.postId = postId;
        this.postImageUrl = postImageUrl;
    }

}