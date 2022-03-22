package cloneproject.Instagram.dto.search;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.dto.member.FollowDTO;
import cloneproject.Instagram.dto.member.MemberDTO;
import cloneproject.Instagram.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMemberDTO extends SearchDTO{
    
    private MemberDTO memberDTO;
    private boolean isFollowing;
    private boolean isFollower;
    private List<FollowDTO> followingMemberFollow;

    @QueryProjection
    public SearchMemberDTO(String dtype, Long count, Member member, boolean isFollowing, boolean isFollower){
        super(dtype, count);
        this.memberDTO = new MemberDTO(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        // this.followingMemberFollow = followingMemberFollow;
    }
    public void setFollowingMemberFollow(List<FollowDTO> followingMemberFollow) {
        this.followingMemberFollow = followingMemberFollow;
    }

}