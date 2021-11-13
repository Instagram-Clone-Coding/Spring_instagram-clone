package cloneproject.Instagram.entity.story;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.vo.Tag;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "story_tags")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "y", column = @Column(name = "story_tag_y")),
            @AttributeOverride(name = "x", column = @Column(name = "story_tag_x"))
    })
    private Tag tag;

    @Builder
    public StoryTag(Member member, Story story, Tag tag) {
        this.member = member;
        this.story = story;
        this.tag = tag;
    }
}