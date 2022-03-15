package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeMembersDTO {

    private MemberDTO member;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean hasStory;

    @QueryProjection
    public LikeMembersDTO(Member member, boolean isFollowing, boolean isFollower) {
        this.member = new MemberDTO(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.hasStory = false;
    }
}
