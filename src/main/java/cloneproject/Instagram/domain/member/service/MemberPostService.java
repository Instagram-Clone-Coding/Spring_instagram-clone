package cloneproject.Instagram.domain.member.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.domain.feed.dto.MemberPostDTO;
import cloneproject.Instagram.domain.feed.dto.PostImageDTO;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberPostService {

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final BlockRepository blockRepository;
	private final PostImageRepository postImageRepository;

	public Page<MemberPostDTO> getMemberPostDTOs(String username, int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Page.empty();
		}

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberPostDTOs(username, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts;

	}

	public List<MemberPostDTO> getRecent15PostDTOs(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Collections.emptyList();
		}

		final Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDTO> posts = memberRepository.getMemberPostDTOs(username, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts.getContent();
	}

	public Page<MemberPostDTO> getMemberSavedPostDTOs(int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberId();

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberSavedPostDTOs(loginMemberId, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts;
	}

	public List<MemberPostDTO> getRecent15SavedPostDTOs() {
		final Long loginMemberId = authUtil.getLoginMemberId();

		final Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDTO> posts = memberRepository.getMemberSavedPostDTOs(loginMemberId, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts.getContent();
	}

	public Page<MemberPostDTO> getMemberTaggedPostDTOs(String username, int size, int page) {
		page = (page == 0 ? 0 : page - 1) + 5;
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Page.empty();
		}

		final Pageable pageable = PageRequest.of(page, size);
		Page<MemberPostDTO> posts = memberRepository.getMemberTaggedPostDTOs(username, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts;

	}

	public List<MemberPostDTO> getRecent15TaggedPostDTOs(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Collections.emptyList();
		}

		final Pageable pageable = PageRequest.of(0, 15);
		Page<MemberPostDTO> posts = memberRepository.getMemberTaggedPostDTOs(username, pageable);
		setMemberPostImageDTOs(posts.getContent());
		return posts.getContent();
	}

	private void setMemberPostImageDTOs(List<MemberPostDTO> memberPostDTOs) {
		final List<Long> postIds = memberPostDTOs.stream()
			.map(MemberPostDTO::getPostId)
			.collect(Collectors.toList());

		final List<PostImageDTO> postImageDTOs = postImageRepository.findAllPostImageDto(postIds);

		final Map<Long, List<PostImageDTO>> postDTOMap = postImageDTOs.stream()
			.collect(Collectors.groupingBy(PostImageDTO::getPostId));

		memberPostDTOs.forEach(p -> p.setPostImageDTO(postDTOMap.get(p.getPostId()).get(0)));
	}

}
