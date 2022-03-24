package cloneproject.Instagram.dto.story;

import cloneproject.Instagram.dto.member.MemberDTO;
import cloneproject.Instagram.entity.story.Story;
import cloneproject.Instagram.vo.Image;
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
