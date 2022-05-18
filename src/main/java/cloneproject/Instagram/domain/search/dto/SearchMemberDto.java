package cloneproject.Instagram.domain.search.dto;

import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchMemberDto extends SearchDto {
    
    private MemberDto member;
    private boolean isFollowing;
    private boolean isFollower;
    private List<FollowDto> followingMemberFollow;

    @QueryProjection
    public SearchMemberDto(String dtype, Member member, boolean isFollowing, boolean isFollower) {
        super(dtype);
        this.member = new MemberDto(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        // this.followingMemberFollow = followingMemberFollow;
    }

    public void setFollowingMemberFollow(List<FollowDto> followingMemberFollow) {
        this.followingMemberFollow = followingMemberFollow;
    }

}