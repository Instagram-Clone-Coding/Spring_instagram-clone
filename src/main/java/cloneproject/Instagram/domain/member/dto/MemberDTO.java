package cloneproject.Instagram.domain.member.dto;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.global.vo.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDTO {

	private Long id;
	private String username;
	private String name;
	private Image image;
	private boolean hasStory;

	public MemberDTO(Member member) {
		this.id = member.getId();
		this.username = member.getUsername();
		this.name = member.getName();
		this.image = member.getImage();
		this.hasStory = false;
	}

}
