package cloneproject.Instagram.domain.follow.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.follow.dto.FollowerDto;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.exception.FollowMyselfFailException;
import cloneproject.Instagram.domain.follow.exception.FollowerDeleteFailException;
import cloneproject.Instagram.domain.follow.exception.UnfollowFailException;
import cloneproject.Instagram.domain.follow.exception.UnfollowMyselfFailException;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.ErrorCode;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static cloneproject.Instagram.global.error.ErrorCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final BlockRepository blockRepository;
    private final AlarmService alarmService;
    private final AuthUtil authUtil;

    @Transactional
    public boolean follow(String followMemberUsername) {
        final Long memberId = authUtil.getLoginMemberId();
        final Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

        if (member.getId().equals(followMember.getId()))
            throw new FollowMyselfFailException();
        if (followRepository.existsByMemberIdAndFollowMemberId(member.getId(), followMember.getId()))
            throw new EntityAlreadyExistException(ErrorCode.FOLLOW_ALREADY_EXIST);

        // 차단당했다면
        if (blockRepository.existsByMemberIdAndBlockMemberId(followMember.getId(), member.getId()))
            throw new EntityNotFoundException(MEMBER_NOT_FOUND);

        // 차단했었다면 차단해제
        blockRepository.findByMemberIdAndBlockMemberId(member.getId(), followMember.getId())
            .ifPresent(blockRepository::delete);

        final Follow follow = new Follow(member, followMember);
        followRepository.save(follow);
        alarmService.alert(followMember, follow);

        return true;
    }

    @Transactional
    public boolean unfollow(String followMemberUsername) {
        final Long memberId = authUtil.getLoginMemberId();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
        if (memberId.equals(followMember.getId())) {
            throw new UnfollowMyselfFailException();
        }
        final Follow follow = followRepository
            .findByMemberIdAndFollowMemberId(memberId, followMember.getId())
            .orElseThrow(UnfollowFailException::new);
        alarmService.delete(followMember, follow);
        followRepository.delete(follow);

        return true;
    }

    @Transactional
    public boolean deleteFollower(String followMemberUsername) {
        final Long memberId = authUtil.getLoginMemberId();
        final Member followMember = memberRepository.findByUsername(followMemberUsername)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));
        if (memberId.equals(followMember.getId())) {
            throw new FollowerDeleteFailException();
        }
        final Follow follow = followRepository
            .findByMemberIdAndFollowMemberId(followMember.getId(), memberId)
            .orElseThrow(FollowerDeleteFailException::new);
        followRepository.delete(follow);
        return true;
    }

    @Transactional(readOnly = true)
    public List<FollowerDto> getFollowings(String memberUsername) {
        final Long memberId = authUtil.getLoginMemberId();

        final Member toMember = memberRepository.findByUsername(memberUsername)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

        final List<FollowerDto> result = followRepository.findFollowings(memberId, toMember.getId());

        return result;
    }

    @Transactional(readOnly = true)
    public List<FollowerDto> getFollowers(String memberUsername) {
        final Long memberId = authUtil.getLoginMemberId();

        final Member toMember = memberRepository.findByUsername(memberUsername)
            .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

        final List<FollowerDto> result = followRepository.findFollowers(memberId, toMember.getId());

        return result;
    }

}
