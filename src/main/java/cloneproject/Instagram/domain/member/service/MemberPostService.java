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
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.feed.dto.MemberPostDto;
import cloneproject.Instagram.domain.feed.dto.PostImageDto;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.service.PostService;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.BlockRepository;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPostService {

	private static final int FIRST_PAGE_SIZE = 15;
	private static final int PAGE_OFFSET = 4;

	private final AuthUtil authUtil;
	private final MemberRepository memberRepository;
	private final BlockRepository blockRepository;
	private final PostImageRepository postImageRepository;
	private final PostService postService;

	public Page<MemberPostDto> getMemberPostDtosWithoutLogin(String username, int size, int page) {
		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = getMemberPostDtos(-1L, username, pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(null, content);
		return posts;
	}

	public List<MemberPostDto> getRecent15PostDtosWithoutLogin(String username) {
		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = getMemberPostDtos(-1L, username, pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(null, content);
		return content;
	}

	public Page<MemberPostDto> getMemberPostDtos(String username, int size, int page) {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = getMemberPostDtos(loginMember.getId(), username, pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(loginMember, content);
		return posts;
	}

	public List<MemberPostDto> getRecent15PostDtos(String username) {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = getMemberPostDtos(loginMember.getId(), username, pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(loginMember, content);
		return content;
	}

	public Page<MemberPostDto> getMemberSavedPostDtos(int size, int page) {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = memberRepository.findMemberSavedPostDtos(loginMember.getId(), pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(loginMember, content);
		return posts;
	}

	public List<MemberPostDto> getRecent15SavedPostDtos() {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = memberRepository.findMemberSavedPostDtos(loginMember.getId(), pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		return content;
	}

	public Page<MemberPostDto> getMemberTaggedPostDtos(String username, int size, int page) {
		final Member loginMember = authUtil.getLoginMember();
		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMember.getId(), member.getId())) {
			return Page.empty();
		}

		final Pageable pageable = PageRequest.of(page + PAGE_OFFSET, size);
		final Page<MemberPostDto> posts = memberRepository.findMemberTaggedPostDtos(loginMember.getId(), username,
			pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(loginMember, content);
		return posts;
	}

	public List<MemberPostDto> getRecent15TaggedPostDtos(String username) {
		final Member loginMember = authUtil.getLoginMember();
		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(loginMember.getId(), member.getId())) {
			return Collections.emptyList();
		}

		final Pageable pageable = PageRequest.of(0, FIRST_PAGE_SIZE);
		final Page<MemberPostDto> posts = memberRepository.findMemberTaggedPostDtos(loginMember.getId(), username,
			pageable);
		final List<MemberPostDto> content = posts.getContent();
		setMemberPostImageDtos(content);
		setPostLikesCount(loginMember, content);
		return content;
	}

	private Page<MemberPostDto> getMemberPostDtos(Long memberId, String username, Pageable pageable) {
		final Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND));

		if (blockRepository.isBlockingOrIsBlocked(memberId, member.getId())) {
			return Page.empty();
		}

		return memberRepository.findMemberPostDtos(memberId, username, pageable);
	}

	private void setPostLikesCount(Member loginMember, List<MemberPostDto> content) {
		content.forEach(post -> {
			if (loginMember != null && !post.getMember().getId().equals(loginMember.getId())
				&& !post.isLikeOptionFlag()) {
				final int count = postService.countOfFollowingsFromPostLikes(post.getPostId(), loginMember);
				post.setPostLikesCount(count);
			} else if (post.isPostLikeFlag()) {
				post.setPostLikesCount(post.getPostLikesCount() + 1);
			}
		});
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
