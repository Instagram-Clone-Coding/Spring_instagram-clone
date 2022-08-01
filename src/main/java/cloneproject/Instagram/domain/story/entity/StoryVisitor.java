package cloneproject.Instagram.domain.story.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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