package cloneproject.Instagram.dto.post;

import cloneproject.Instagram.dto.comment.CommentDTO;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long postId;
    private String postContent;
    private LocalDateTime postUploadDate;
    private String memberUsername;
    private String memberNickname;
    private String memberImageUrl;
    private int postLikesCount;
    private boolean postBookmarkFlag;
    private boolean postLikeFlag;
    private String followingMemberUsernameLikedPost;
    private List<PostImageDTO> postImageDTOs = new ArrayList<>();
    private List<CommentDTO> commentDTOs = new ArrayList<>();

    @QueryProjection
    public PostResponse(Long postId, String postContent, LocalDateTime postUploadDate, String memberUsername, String memberNickname, String memberImageUrl, int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, String followingMemberUsernameLikedPost) {
        this.postId = postId;
        this.postContent = postContent;
        this.postUploadDate = postUploadDate;
        this.memberUsername = memberUsername;
        this.memberNickname = memberNickname;
        this.memberImageUrl = memberImageUrl;
        this.postLikesCount = postLikesCount;
        this.postBookmarkFlag = postBookmarkFlag;
        this.postLikeFlag = postLikeFlag;
        this.followingMemberUsernameLikedPost = followingMemberUsernameLikedPost;
    }
}
