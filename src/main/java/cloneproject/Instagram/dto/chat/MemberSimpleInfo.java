package cloneproject.Instagram.dto.chat;

import cloneproject.Instagram.entity.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSimpleInfo {

    private String username;
    private String name;
    private String imageUrl;

    public MemberSimpleInfo(Member member) {
        this.username = member.getUsername();
        this.name = member.getName();
        this.imageUrl = member.getImage().getImageUrl();
    }
}
