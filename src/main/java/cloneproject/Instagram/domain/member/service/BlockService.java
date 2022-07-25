package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.AlreadyBlockException;
import cloneproject.Instagram.domain.member.exception.CantBlockMyselfException;
import cloneproject.Instagram.domain.member.exception.CantUnblockException;
import cloneproject.Instagram.domain.member.exception.CantUnblockMyselfException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

	private final AuthUtil authUtil;
	private final BlockRepository blockRepository;
	private final MemberRepository memberRepository;
	private final FollowRepository followRepository;

	@Transactional
	public boolean block(String blockMemberUsername) {
		final Member member = authUtil.getLoginMember();
		final Member blockMember = memberRepository.findByUsername(blockMemberUsername)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (member.getId().equals(blockMember.getId())) {
			throw new CantBlockMyselfException();
		}
		if (blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), blockMember.getId())) {
			throw new AlreadyBlockException();
		}

		// 팔로우가 되어있었다면 언팔로우
		Optional<Follow> follow = followRepository.findByMemberIdAndFollowMemberId(member.getId(), blockMember.getId());
		follow.ifPresent(followRepository::delete);

		follow = followRepository.findByMemberIdAndFollowMemberId(blockMember.getId(), member.getId());
		follow.ifPresent(followRepository::delete);

		final Block block = new Block(member, blockMember);
		blockRepository.save(block);
		return true;
	}

	@Transactional
	public boolean unblock(String blockMemberUsername) {
		final Long memberId = authUtil.getLoginMemberId();
		final Member blockMember = memberRepository.findByUsername(blockMemberUsername)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (memberId.equals(blockMember.getId())) {
			throw new CantUnblockMyselfException();
		}

		final Block block = blockRepository.findByMemberIdAndBlockMemberId(memberId, blockMember.getId())
			.orElseThrow(CantUnblockException::new);
		blockRepository.delete(block);
		return true;
	}

}
