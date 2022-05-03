package cloneproject.Instagram.domain.story.dto;

import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.global.vo.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryDto {

    private Long id;
    private Image image;
    private LocalDateTime uploadDate;
    private MemberDTO member;

    public StoryDto(Story story) {
        this.id = story.getId();
        this.image = story.getImage();
        this.uploadDate = story.getUploadDate();
        this.member = new MemberDTO(story.getMember());
    }
}
