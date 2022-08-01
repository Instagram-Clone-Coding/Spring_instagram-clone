package cloneproject.Instagram.domain.story.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.global.vo.Image;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryDto {

	private Long id;
	private Image image;
	private LocalDateTime uploadDate;
	private MemberDto member;

	public StoryDto(Story story) {
		this.id = story.getId();
		this.image = story.getImage();
		this.uploadDate = story.getUploadDate();
		this.member = new MemberDto(story.getMember());
	}

}
