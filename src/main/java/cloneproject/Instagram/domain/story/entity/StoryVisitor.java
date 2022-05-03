package cloneproject.Instagram.domain.story.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@Entity
@Table(name = "story_visitors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoryVisitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "story_visitor_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public StoryVisitor(Story story, Member member) {
        this.story = story;
        this.member = member;
    }
}