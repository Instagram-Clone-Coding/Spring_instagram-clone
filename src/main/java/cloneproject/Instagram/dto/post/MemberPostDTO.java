package cloneproject.Instagram.dto.post;


import cloneproject.Instagram.vo.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostDTO {

    private Long postId;
    private Image postImage;
    private boolean hasManyPosts;
    private int postCommentsCount;
    private int postLikesCount;

    @Builder
    public MemberPostDTO(Long postId, Image postImage, boolean hasManyPosts, int postCommentsCount, int postLikesCount) {
        this.postId = postId;
        this.postImage = postImage;
        this.hasManyPosts = hasManyPosts;
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
    }
}