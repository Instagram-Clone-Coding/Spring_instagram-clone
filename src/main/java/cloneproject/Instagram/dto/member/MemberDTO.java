package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.vo.Image;
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
