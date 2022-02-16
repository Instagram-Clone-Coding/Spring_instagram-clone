package cloneproject.Instagram.entity.comment;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "recent_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public RecentComment(Member member, Post post, Comment comment) {
        this.member = member;
        this.post = post;
        this.comment = comment;
    }
}
