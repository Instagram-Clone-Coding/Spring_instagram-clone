package cloneproject.Instagram.entity.member;

import cloneproject.Instagram.entity.story.Story;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "member_stories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_story_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Builder
    public MemberStory(Member member, Story story) {
        this.member = member;
        this.story = story;
    }
}
