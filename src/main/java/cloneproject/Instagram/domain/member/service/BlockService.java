package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import cloneproject.Instagram.domain.follow.repository.FollowRepository;
import cloneproject.Instagram.domain.member.entity.Block;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.BlockMyselfFailException;
import cloneproject.Instagram.domain.member.exception.UnblockFailException;
import cloneproject.Instagram.domain.member.exception.UnblockMyselfFailException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
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
			throw new BlockMyselfFailException();
		}
		if (blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), blockMember.getId())) {
			throw new EntityAlreadyExistException(BLOCK_ALREADY_EXIST);
		}

		followRepository.findByMemberIdAndFollowMemberId(member.getId(), blockMember.getId())
			.ifPresent(followRepository::delete);

		followRepository.findByMemberIdAndFollowMemberId(blockMember.getId(), member.getId())
			.ifPresent(followRepository::delete);

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
			throw new UnblockMyselfFailException();
		}

		final Block block = blockRepository.findByMemberIdAndBlockMemberId(memberId, blockMember.getId())
			.orElseThrow(UnblockFailException::new);
		blockRepository.delete(block);
		return true;
	}

}
