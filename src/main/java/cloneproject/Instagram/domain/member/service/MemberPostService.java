package cloneproject.Instagram.domain.member.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPostService {

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final BlockRepository blockRepository;

	public Page<MemberPostDTO> getMemberPostDto(String username, int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
				.orElseThrow(MemberDoesNotExistException::new);

		if (blockRepository.existsByMemberIdAndBlockMemberId(loginMemberId, member.getId()) ||
				blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginMemberId)) {
			return null;
		}

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberPostDto(loginMemberId, username, pageable);
		return posts;

	}

	public List<MemberPostDTO> getRecent15PostDTOs(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
				.orElseThrow(MemberDoesNotExistException::new);

		if (blockRepository.existsByMemberIdAndBlockMemberId(loginMemberId, member.getId()) ||
				blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginMemberId)) {
			return null;
		}

		List<MemberPostDTO> posts = memberRepository.getRecent15PostDTOs(loginMemberId, username);
		return posts;
	}

	public Page<MemberPostDTO> getMemberSavedPostDto(int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberId();

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberSavedPostDto(loginMemberId, pageable);
		return posts;
	}

	public List<MemberPostDTO> getRecent15SavedPostDTOs() {
		final Long loginMemberId = authUtil.getLoginMemberId();

		List<MemberPostDTO> posts = memberRepository.getRecent15SavedPostDTOs(loginMemberId);
		return posts;
	}

	public Page<MemberPostDTO> getMemberTaggedPostDto(String username, int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
				.orElseThrow(MemberDoesNotExistException::new);

		if (blockRepository.existsByMemberIdAndBlockMemberId(loginMemberId, member.getId()) ||
				blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginMemberId)) {
			return null;
		}

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberTaggedPostDto(loginMemberId, username, pageable);
		return posts;

	}

	public List<MemberPostDTO> getRecent15TaggedPostDTOs(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
				.orElseThrow(MemberDoesNotExistException::new);

		if (blockRepository.existsByMemberIdAndBlockMemberId(loginMemberId, member.getId()) ||
				blockRepository.existsByMemberIdAndBlockMemberId(member.getId(), loginMemberId)) {
			return null;
		}

		List<MemberPostDTO> posts = memberRepository.getRecent15TaggedPostDTOs(loginMemberId, username);
		return posts;
	}

}
