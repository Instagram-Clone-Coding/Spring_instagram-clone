package cloneproject.Instagram.dto.post;

import cloneproject.Instagram.dto.comment.CommentDTO;
import cloneproject.Instagram.dto.member.MemberDTO;
import cloneproject.Instagram.entity.member.Member;
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
    private MemberDTO member;
    private int postCommentsCount;
    private int postLikesCount;
    private boolean postBookmarkFlag;
    private boolean postLikeFlag;
    private boolean commentFlag;
    private String followingMemberUsernameLikedPost;
    private List<CommentDTO> recentComments = new ArrayList<>();

    @QueryProjection
    public PostDTO(Long postId, String postContent, LocalDateTime postUploadDate, Member member, int postCommentsCount, int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentFlag) {
        this.postId = postId;
        this.postContent = postContent;
        this.postUploadDate = postUploadDate;
        this.member = new MemberDTO(member);
        this.postCommentsCount = postCommentsCount;
        this.postLikesCount = postLikesCount;
        this.postBookmarkFlag = postBookmarkFlag;
        this.postLikeFlag = postLikeFlag;
        this.commentFlag = commentFlag;
    }
}
