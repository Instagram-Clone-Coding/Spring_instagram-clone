package cloneproject.Instagram.domain.feed.dto;

import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.member.entity.Member;

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
    private MemberDTO member;
    private String content;
    private LocalDateTime uploadDate;
    private int commentLikesCount;
    private boolean commentLikeFlag;
    private int repliesCount;

    @QueryProjection
    public CommentDTO(Long postId, Long id, Member member, String content, LocalDateTime uploadDate, int commentLikesCount, boolean commentLikeFlag, int repliesCount) {
        this.postId = postId;
        this.id = id;
        this.member = new MemberDTO(member);
        this.content = content;
        this.uploadDate = uploadDate;
        this.commentLikesCount = commentLikesCount;
        this.commentLikeFlag = commentLikeFlag;
        this.repliesCount = repliesCount;
    }
}
