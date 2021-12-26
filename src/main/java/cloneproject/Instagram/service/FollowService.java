package cloneproject.Instagram.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.AlreadyFollowException;
import cloneproject.Instagram.exception.CantFollowMyselfException;
import cloneproject.Instagram.exception.CantUnfollowException;
import cloneproject.Instagram.exception.CantUnfollowMyselfException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.FollowRepository;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.vo.FollowerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean follow(String followMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId))
                                        .orElseThrow(MemberDoesNotExistException::new);
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        if(member.getId().equals(followMember.getId())){
            throw new CantFollowMyselfException();
        }
        if(followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId())){
            throw new AlreadyFollowException();
        }
        Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        return true;
    }

    @Transactional
    public boolean unfollow(String followMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        if(Long.valueOf(memberId).equals(followMember.getId())){
            throw new CantUnfollowMyselfException();
        }
        Follow follow = followRepository.findByMemberIdAndFollowMemberId(Long.valueOf(memberId), followMember.getId())
                                        .orElseThrow(CantUnfollowException::new);
        followRepository.delete(follow);
        return true;
    }

    @Transactional(readOnly = true)
    public List<FollowerInfo> getFollowings(String memberUsername){ 
        final Member member = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        final List<Follow> follows = followRepository.findAllByMemberId(member.getId());
        final List<Member> followingMembers = follows.stream()
                                                .map(follow->follow.getFollowMember())
                                                .collect(Collectors.toList());
        final List<FollowerInfo> result = followingMembers.stream()
                                                .map(this::convertMemberToFollowerInfo)
                                                .collect(Collectors.toList());
        return result;
    }

    @Transactional(readOnly = true)
    public List<FollowerInfo> getFollowers(String memberUsername){ 
        final Member member = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        final List<Follow> follows = followRepository.findAllByFollowMemberId(member.getId());
        final List<Member> followingMembers = follows.stream()
                                                .map(follow->follow.getMember())
                                                .collect(Collectors.toList());
        final List<FollowerInfo> result = followingMembers.stream()
                                                .map(this::convertMemberToFollowerInfo)
                                                .collect(Collectors.toList());
        return result;
    }


    @Transactional(readOnly = true)
    public Integer getFollowingsCount(String memberUsername){ 
        final Member member = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        final List<Follow> follows = followRepository.findAllByMemberId(member.getId());
        return follows.size();
    }

    @Transactional(readOnly = true)
    public Integer getFollowersCount(String memberUsername){ 
        final Member member = memberRepository.findByUsername(memberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        final List<Follow> follows = followRepository.findAllByFollowMemberId(member.getId());
        return follows.size();
    }

    /**
     * member_id(pk)만을 담은 팔로잉 목록
     * 메인화면(포스트목록) 구현 때 사용
     * @return List<Long>: 로그인한 사용자가 팔로우중인 멤버들의 id 목록
     */
    @Transactional(readOnly = true)
    public List<Long> getOnlyFollowingsMemberId(){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final List<Follow> follows = followRepository.findAllByMemberId(Long.valueOf(memberId));
        final List<Long> result = follows.stream()
                                        .map(follow->follow.getFollowMember().getId())
                                        .collect(Collectors.toList());
        return result;

    }

    /**
     * 프로필, 게시물에서 팔로우 여부를 판단하기 위한 메서드
     * @param memberId 멤버ID (현재 로그인 중인 사용자의 PK)
     * @param followMemberUsername 대상의 username
     * @return boolean: 팔로우 여부
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(String followMemberUsername){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                                                .orElseThrow(MemberDoesNotExistException::new);
        return followRepository.existsByMemberIdAndFollowMemberId(Long.valueOf(memberId), followMember.getId());
    }

    private FollowerInfo convertMemberToFollowerInfo(Member member){
        final FollowerInfo result = FollowerInfo.builder()
                                    .username(member.getUsername())
                                    .name(member.getName())
                                    .image(member.getImage())
                                    .isFollowing(isFollowing(member.getUsername()))
                                    //TODO story 여부 판단함수
                                    .hasStory(false)
                                    .build();
        return result;
    }

}
