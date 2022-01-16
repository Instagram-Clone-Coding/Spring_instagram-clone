package cloneproject.Instagram.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDTO {

    @JsonIgnore
    private Long postId;
    private Long id;
    private String username;
    private String content;
    private LocalDateTime uploadDate;
    private int commentLikesCount;
    private boolean commentLikeFlag;
    private int repliesCount;

    @QueryProjection
    public CommentDTO(Long postId, Long id, String username, String content, LocalDateTime uploadDate, int commentLikesCount, boolean commentLikeFlag, int repliesCount) {
        this.postId = postId;
        this.id = id;
        this.username = username;
        this.content = content;
        this.uploadDate = uploadDate;
        this.commentLikesCount = commentLikesCount;
        this.commentLikeFlag = commentLikeFlag;
        this.repliesCount = repliesCount;
    }
}
