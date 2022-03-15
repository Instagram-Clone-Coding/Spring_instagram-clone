package cloneproject.Instagram.dto.member;

import cloneproject.Instagram.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowerDTO {

    private MemberDTO member;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean isMe;

    @QueryProjection
    public FollowerDTO(Member member, boolean isFollowing, boolean isFollower, boolean isMe){
        this.member = new MemberDTO(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.isMe = isMe;
    }

}
