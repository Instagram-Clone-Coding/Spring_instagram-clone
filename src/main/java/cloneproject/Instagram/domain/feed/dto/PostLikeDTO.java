package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeDTO {

    private Long postId;
    private String username;

    @QueryProjection
    public PostLikeDTO(Long postId, String username) {
        this.postId = postId;
        this.username = username;
    }
}
