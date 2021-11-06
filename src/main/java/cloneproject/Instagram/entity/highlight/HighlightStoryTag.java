package cloneproject.Instagram.entity.highlight;

import cloneproject.Instagram.entity.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "highlight_story_tags")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HighlightStoryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_story_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highlight_story_id")
    private HighlightStory highlightStory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "highlight_story_tag_y")
    private Long storyTagY;

    @Column(name = "highlight_story_tag_x")
    private Long storyTagX;

    @Builder
    public HighlightStoryTag(HighlightStory highlightStory, Member member, Long storyTagY, Long storyTagX) {
        this.highlightStory = highlightStory;
        this.member = member;
        this.storyTagY = storyTagY;
        this.storyTagX = storyTagX;
    }
}
