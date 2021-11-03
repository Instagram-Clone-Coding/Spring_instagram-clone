package cloneproject.Instagram.entity.post;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.vo.Tag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "post_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_image_id")
    private PostImage image;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "y", column = @Column(name = "post_tag_y")),
            @AttributeOverride(name = "x", column = @Column(name = "post_tag_x"))
    })
    private Tag tag;

    @Builder
    public PostTag(Member member, PostImage image, Tag tag) {
        this.member = member;
        this.image = image;
        this.tag = tag;
    }
}
