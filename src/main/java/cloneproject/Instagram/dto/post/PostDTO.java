package cloneproject.Instagram.dto.post;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDTO {

    private Long postId;
    private String postContent;
    private List<PostImageDTO> postImageDTOs = new ArrayList<>();
    private LocalDateTime postUploadDate;
    private String memberUsername;
    private String memberNickname;
    private String memberImageUrl;
    private int postCommentsCount;
    private int postLikesCount;
    private boolean postBookmarkFlag;
    private boolean postLikeFlag;
    private String followingMemberUsernameLikedPost;

    @QueryProjection
    public PostDTO(Long postId, String postContent, LocalDateTime postUploadDate, String memberUsername, String memberNickname, String memberImageUrl, int postCommentsCount, int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag) {
        this.postId = postId;
        this.postContent = postContent;
        this.postUploadDate = postUploadDate;
        this.memberUsername = memberUsername;
        this.memberNickname = memberNickname;
        this.memberImageUrl = memberImageUrl;
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
        this.postBookmarkFlag = postBookmarkFlag;
        this.postLikeFlag = postLikeFlag;
    }
}
