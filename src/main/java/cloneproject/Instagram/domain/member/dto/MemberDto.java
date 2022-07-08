package cloneproject.Instagram.domain.member.dto;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.vo.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {

	private Long id;
	private String username;
	private String name;
	private Image image;
	private boolean hasStory;

	public void setHasStory(boolean hasStory) {
		this.hasStory = hasStory;
	}

	public MemberDto(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
		this.name = member.getName();
		this.image = member.getImage();
		this.hasStory = false;
	}

}
