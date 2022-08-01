package cloneproject.Instagram.domain.dm.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.entity.Story;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STORY")
@Table(name = "message_stories")
public class MessageStory extends Message {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "story_id")
	private Story story;

	public MessageStory(Story story, Member member, Room room) {
		super(member, room);
		this.story = story;
	}

}
