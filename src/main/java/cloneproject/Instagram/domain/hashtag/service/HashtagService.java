package cloneproject.Instagram.domain.hashtag.service;

import static cloneproject.Instagram.global.error.ErrorCode.*;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.follow.exception.HashtagFollowFailException;
import cloneproject.Instagram.domain.follow.exception.HashtagUnfollowFailException;
import cloneproject.Instagram.domain.follow.repository.HashtagFollowRepository;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;
import cloneproject.Instagram.domain.hashtag.repository.HashtagPostRepository;
import cloneproject.Instagram.domain.hashtag.repository.HashtagRepository;
import cloneproject.Instagram.domain.follow.entity.HashtagFollow;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.search.service.SearchService;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.StringExtractUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

	private final HashtagRepository hashtagRepository;
	private final HashtagPostRepository hashtagPostRepository;
	private final HashtagFollowRepository hashtagFollowRepository;
	private final SearchService searchService;
	private final StringExtractUtil stringExtractUtil;
	private final AuthUtil authUtil;

	@Transactional
	public void followHashtag(String hashtagName) {
		final Member loginMember = authUtil.getLoginMember();
		final Hashtag hashtag = getHashtag(hashtagName);

		if (hashtagFollowRepository.existsByMemberIdAndHashtagId(loginMember.getId(), hashtag.getId())) {
			throw new HashtagFollowFailException();
		}

		final HashtagFollow hashtagFollow = HashtagFollow.builder()
			.member(loginMember)
			.hashtag(hashtag)
			.build();

		hashtagFollowRepository.save(hashtagFollow);
	}

	@Transactional
	public void unfollowHashtag(String hashtagName) {
		final Member loginMember = authUtil.getLoginMember();
		final Hashtag hashtag = getHashtag(hashtagName);

		final HashtagFollow hashtagFollow =
			hashtagFollowRepository.findByMemberIdAndHashtagId(loginMember.getId(), hashtag.getId())
				.orElseThrow(HashtagUnfollowFailException::new);

		hashtagFollowRepository.delete(hashtagFollow);
	}

	@Transactional
	public void registerHashtags(Post post) {
		registerHashtags(post, post.getContent());
	}

	@Transactional
	public void registerHashtags(Post post, Comment comment) {
		registerHashtags(post, comment.getContent());
	}

	private void registerHashtags(Post post, String content) {
		final Set<String> names = new HashSet<>(stringExtractUtil.extractHashtags(content));
		final Map<String, Hashtag> hashtagMap = hashtagRepository.findAllByNameIn(names).stream()
			.collect(Collectors.toMap(Hashtag::getName, h -> h));
		final List<HashtagPost> newHashtagPost = new ArrayList<>();

		names.forEach(name -> {
			final Hashtag hashtag;
			if (hashtagMap.containsKey(name)) {
				hashtag = hashtagMap.get(name);
				hashtag.upCount();
			} else {
				hashtag = hashtagRepository.save(new Hashtag(name));
				searchService.createSearchHashtag(hashtag);
			}

			if (hashtagPostRepository.findByHashtagAndPost(hashtag, post).isEmpty()) {
				newHashtagPost.add(new HashtagPost(hashtag, post));
			}
		});

		hashtagPostRepository.saveAllBatch(newHashtagPost);
	}

	@Transactional
	public void unregisterHashtagsByDeletingPost(Post post) {
		final Set<String> names = new HashSet<>(stringExtractUtil.extractHashtags(post.getContent()));
		final Map<String, Hashtag> hashtagMap = hashtagRepository.findAllByNameIn(names).stream()
			.collect(Collectors.toMap(Hashtag::getName, h -> h));
		final List<Hashtag> deleteHashtags = new ArrayList<>();

		names.forEach(name -> {
			final Hashtag hashtag = hashtagMap.get(name);
			if (hashtag.getCount() == 1) {
				deleteHashtags.add(hashtag);
			} else {
				hashtag.downCount();
			}
		});

		final List<HashtagPost> hashtagPosts = hashtagPostRepository.findAllByPost(post);

		hashtagPostRepository.deleteAllInBatch(hashtagPosts);
		searchService.deleteSearchHashtags(deleteHashtags);
		hashtagRepository.deleteAllInBatch(deleteHashtags);
	}

	@Transactional
	public void unregisterHashtagsByDeletingComments(Post post, List<Comment> comments) {
		final Set<String> names = new HashSet<>();
		final Map<String, Integer> countMap = new LinkedHashMap<>();

		comments.forEach(comment -> {
			final List<String> allNames = stringExtractUtil.extractHashtags(comment.getContent());
			allNames.forEach(n -> {
				if (countMap.containsKey(n)) {
					countMap.replace(n, countMap.get(n) + 1);
				} else {
					countMap.put(n, 1);
				}
			});
			names.addAll(allNames);
		});

		final Map<String, Hashtag> hashtagMap = hashtagRepository.findAllByNameIn(names).stream()
			.collect(Collectors.toMap(Hashtag::getName, h -> h));
		final List<Hashtag> deleteHashtags = new ArrayList<>();

		names.forEach(name -> {
			final Hashtag hashtag = hashtagMap.get(name);
			final Integer count = countMap.get(name);
			if (hashtag.getCount().equals(count)) {
				deleteHashtags.add(hashtag);
			} else {
				hashtag.downCount(count);
			}
		});

		final List<HashtagPost> hashtagPosts = hashtagPostRepository.findAllByPostAndHashtagIn(post, deleteHashtags);

		hashtagPostRepository.deleteAllInBatch(hashtagPosts);
		searchService.deleteSearchHashtags(deleteHashtags);
		hashtagRepository.deleteAllInBatch(deleteHashtags);
	}

	private Hashtag getHashtag(String hashtagName) {
		return hashtagRepository.findByName(hashtagName.substring(1))
			.orElseThrow(() -> new EntityNotFoundException(HASHTAG_NOT_FOUND));
	}
}
