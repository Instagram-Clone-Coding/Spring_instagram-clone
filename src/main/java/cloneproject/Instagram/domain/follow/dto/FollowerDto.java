package cloneproject.Instagram.domain.follow.dto;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class FollowerDto {

    private MemberDto member;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean isMe;

    @QueryProjection
    public FollowerDto(Member member, boolean isFollowing, boolean isFollower, boolean isMe){
        this.member = new MemberDto(member);
        this.isFollowing = isFollowing;
        this.isFollower = isFollower;
        this.isMe = isMe;
    }

}
