package cloneproject.Instagram.domain.search.dto;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.follow.dto.FollowDTO;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.member.entity.Member;
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