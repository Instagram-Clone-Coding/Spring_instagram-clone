package cloneproject.Instagram.domain.follow.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.exception.AlreadyFollowException;
import cloneproject.Instagram.domain.follow.exception.CantDeleteFollowerException;
import cloneproject.Instagram.domain.follow.exception.CantFollowMyselfException;
import cloneproject.Instagram.domain.follow.exception.CantUnfollowException;
import cloneproject.Instagram.domain.follow.exception.CantUnfollowMyselfException;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    private final AlarmService alarmService;

    @Transactional
    public boolean follow(String followMemberUsername) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(MemberDoesNotExistException::new);
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                .orElseThrow(MemberDoesNotExistException::new);

        if (member.getId().equals(followMember.getId()))
            throw new CantFollowMyselfException();
        if (followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId()))
            throw new AlreadyFollowException();

        // 차단당했다면
        if (blockRepository.existsByMemberIdAndBlockMemberId(followMember.getId(), member.getId()))
            throw new MemberDoesNotExistException();

        // 차단했었다면 차단해제
        blockRepository.findByMemberIdAndBlockMemberId(member.getId(), followMember.getId()).ifPresent(blockRepository::delete);

        final Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        alarmService.alert(followMember, follow);

        return true;
    }

    @Transactional
    public boolean unfollow(String followMemberUsername) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                .orElseThrow(MemberDoesNotExistException::new);
        if (Long.valueOf(memberId).equals(followMember.getId())) {
            throw new CantUnfollowMyselfException();
        }
        final Follow follow = followRepository.findByMemberIdAndFollowMemberId(Long.valueOf(memberId), followMember.getId())
                .orElseThrow(CantUnfollowException::new);
        alarmService.delete(followMember, follow);
        followRepository.delete(follow);

        return true;
    }

    @Transactional
    public boolean deleteFollower(String followMemberUsername) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
                .orElseThrow(MemberDoesNotExistException::new);
        if (Long.valueOf(memberId).equals(followMember.getId())) {
            throw new CantDeleteFollowerException();
        }
        final Follow follow = followRepository.findByMemberIdAndFollowMemberId(followMember.getId(), Long.valueOf(memberId))
            .orElseThrow(CantDeleteFollowerException::new);
        followRepository.delete(follow);
        return true;
    }

    @Transactional(readOnly = true)
    public List<FollowerDto> getFollowings(String memberUsername) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        final Member toMember = memberRepository.findByUsername(memberUsername)
                .orElseThrow(MemberDoesNotExistException::new);

        final List<FollowerDto> result = followRepository.getFollowings(Long.valueOf(memberId), toMember.getId());

        return result;
    }

    @Transactional(readOnly = true)
    public List<FollowerDto> getFollowers(String memberUsername) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        final Member toMember = memberRepository.findByUsername(memberUsername)
                .orElseThrow(MemberDoesNotExistException::new);

        final List<FollowerDto> result = followRepository.getFollowers(Long.valueOf(memberId), toMember.getId());

        return result;
    }

}
