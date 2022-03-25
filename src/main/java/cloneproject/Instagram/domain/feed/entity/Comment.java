package cloneproject.Instagram.domain.feed.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import cloneproject.Instagram.domain.member.entity.Member;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Lob
    @Column(name = "comment_content")
    private String content;

    @CreatedDate
    @Column(name = "comment_upload_date")
    private LocalDateTime uploadDate;

    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Builder
    public Comment(Comment parent, Member member, Post post, String content) {
        this.parent = (parent == null ? this : parent);
        this.member = member;
        this.post = post;
        this.content = content;
    }
}
