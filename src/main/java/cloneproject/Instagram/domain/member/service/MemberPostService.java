package cloneproject.Instagram.domain.member.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.feed.dto.PostImageDto;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberPostService {

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final BlockRepository blockRepository;
	private final PostImageRepository postImageRepository;
	private static final int FIRST_PAGE_SIZE = 15;
	private static final int PAGE_OFFSET = 4;

	public Page<MemberPostDto> getMemberPostDtos(String username, int size, int page) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Page.empty();
		}

		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = memberRepository.findMemberPostDtos(username, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts;
	}

	public List<MemberPostDto> getRecent15PostDtos(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Collections.emptyList();
		}

		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = memberRepository.findMemberPostDtos(username, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts.getContent();
	}

	public Page<MemberPostDto> getMemberSavedPostDtos(int size, int page) {
		final Long loginMemberId = authUtil.getLoginMemberId();

		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = memberRepository.findMemberSavedPostDtos(loginMemberId, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts;
	}

	public List<MemberPostDto> getRecent15SavedPostDtos() {
		final Long loginMemberId = authUtil.getLoginMemberId();

		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = memberRepository.findMemberSavedPostDtos(loginMemberId, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts.getContent();
	}

	public Page<MemberPostDto> getMemberTaggedPostDtos(String username, int size, int page) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Page.empty();
		}

		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = memberRepository.findMemberTaggedPostDtos(username, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts;
	}

	public List<MemberPostDto> getRecent15TaggedPostDtos(String username) {
		final Long loginMemberId = authUtil.getLoginMemberIdOrNull();

		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMemberId, member.getId())) {
			return Collections.emptyList();
		}

		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = memberRepository.findMemberTaggedPostDtos(username, pageable);
		setMemberPostImageDtos(posts.getContent());
		return posts.getContent();
	}

	private void setMemberPostImageDtos(List<MemberPostDto> memberPostDtos) {
		final List<Long> postIds = memberPostDtos.stream()
			.map(MemberPostDto::getPostId)
			.collect(Collectors.toList());

		final List<PostImageDto> postImageDtos = postImageRepository.findAllPostImageDto(postIds);

		final Map<Long, List<PostImageDto>> postDTOMap = postImageDtos.stream()
			.collect(Collectors.groupingBy(PostImageDto::getPostId));

		memberPostDtos.forEach(p -> p.setPostImages(postDTOMap.get(p.getPostId()).get(0)));
	}

}
