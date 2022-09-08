package cloneproject.Instagram.domain.feed.service;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;
import static cloneproject.Instagram.global.error.ErrorCode.*;
import static cloneproject.Instagram.global.util.ConstantUtils.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.dm.service.MessageService;
import cloneproject.Instagram.domain.feed.dto.CommentDto;
import cloneproject.Instagram.domain.feed.dto.PostDto;
import cloneproject.Instagram.domain.feed.dto.PostImageDto;
import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import cloneproject.Instagram.domain.feed.dto.PostLikeCountDto;
import cloneproject.Instagram.domain.feed.dto.PostLikeDto;
import cloneproject.Instagram.domain.feed.dto.PostTagDto;
import cloneproject.Instagram.domain.feed.dto.PostUploadRequest;
import cloneproject.Instagram.domain.feed.dto.PostUploadResponse;
import cloneproject.Instagram.domain.feed.entity.Bookmark;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.feed.exception.CantDeletePostException;
import cloneproject.Instagram.domain.feed.repository.BookmarkRepository;
import cloneproject.Instagram.domain.feed.repository.CommentRepository;
import cloneproject.Instagram.domain.feed.repository.PostImageRepository;
import cloneproject.Instagram.domain.feed.repository.PostLikeRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.feed.repository.PostTagRepository;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.follow.service.FollowService;
import cloneproject.Instagram.domain.hashtag.repository.HashtagPostRepository;
import cloneproject.Instagram.domain.hashtag.repository.HashtagRepository;
import cloneproject.Instagram.domain.hashtag.service.HashtagService;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.mention.service.MentionService;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.domain.story.service.MemberStoryService;
import cloneproject.Instagram.global.error.ErrorResponse.FieldError;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.error.exception.InvalidInputException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.StringExtractUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

	private final AuthUtil authUtil;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostImageRepository postImageRepository;
	private final PostTagRepository postTagRepository;
	private final BookmarkRepository bookmarkRepository;
	private final CommentRepository commentRepository;
	private final HashtagRepository hashtagRepository;
	private final HashtagPostRepository hashtagPostRepository;
	private final AlarmService alarmService;
	private final CommentService commentService;
	private final MentionService mentionService;
	private final HashtagService hashtagService;
	private final MessageService messageService;
	private final PostLikeService postLikeService;
	private final PostImageService postImageService;
	private final FollowService followService;
	private final MemberStoryService memberStoryService;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final StringExtractUtil stringExtractUtil;

	@Transactional
	public PostUploadResponse upload(PostUploadRequest request) {
		final List<MultipartFile> postImages = request.getPostImages();
		final List<String> altTexts = request.getAltTexts();
		final List<PostImageTagRequest> postImageTags = request.getPostImageTags();
		validateParameters(postImages.size(), altTexts.size(), postImageTags);

		final Member loginMember = authUtil.getLoginMember();
		final Post post = Post.builder()
			.content(request.getContent())
			.member(loginMember)
			.commentFlag(request.isCommentFlag())
			.likeFlag(request.isLikeFlag())
			.build();

		postRepository.save(post);
		postImageService.saveAll(post, request.getPostImages(), request.getAltTexts(), request.getPostImageTags());
		hashtagService.registerHashtags(post);
		mentionService.mentionMembers(loginMember, post);

		final Set<String> taggedMemberUsernames = request.getPostImageTags().stream()
			.map(PostImageTagRequest::getUsername)
			.collect(toSet());
		taggedMemberUsernames.remove(loginMember.getUsername());
		messageService.sendMessageToMembersIndividually(loginMember, post, taggedMemberUsernames);

		return new PostUploadResponse(post.getId());
	}

	@Transactional
	public void delete(Long postId) {
		final Member loginMember = authUtil.getLoginMember();
		final Post post = getPostWithMember(postId);

		if (!post.getMember().getId().equals(loginMember.getId())) {
			throw new CantDeletePostException();
		}

		alarmService.deleteAll(post);
		hashtagService.unregisterHashtagsByDeletingPost(post);
		mentionService.deleteAll(post);
		postLikeService.deleteAll(post);
		postImageService.deleteAll(post);
		commentService.deleteAllInPost(post);
		messageService.deleteMessagePosts(post);
		postRepository.delete(post);
	}

	public Page<PostDto> getPostDtoPage(int size, int page) {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(page, size);
		final Page<PostDto> postDtoPage = postRepository.findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(
			loginMember.getId(), pageable);
		setContents(loginMember, postDtoPage.getContent());
		return postDtoPage;
	}

	public PostDto getPostDto(Long postId) {
		final Member loginMember = authUtil.getLoginMember();
		final PostDto postDto = postRepository.findPostDtoByPostIdAndMemberId(postId, loginMember.getId())
			.orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
		setContent(loginMember, postDto);
		return postDto;
	}

	public PostDto getPostDtoWithoutLogin(Long postId) {
		final PostDto postDto = postRepository.findPostDtoWithoutLoginByPostId(postId)
			.orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
		setContentWithoutLogin(postDto);
		return postDto;
	}

	/**
	 * 해당 게시물을 좋아요한 사람들 중, <br>
	 * 현재 로그인한 유저가 팔로우하고 있는 사람들의 수를 구한다.
	 */
	public int countOfFollowingsFromPostLikes(Long postId, Member loginMember) {
		final Set<Member> postLikeMembers = postLikeService.getAllWithMember(postId).stream()
			.map(PostLike::getMember)
			.collect(toSet());

		final List<Member> followings = followService.getFollowings(loginMember).stream()
			.map(Follow::getFollowMember)
			.collect(toList());

		int count = 0;
		for (Member following : followings) {
			if (postLikeMembers.contains(following)) {
				count++;
			}
		}

		if (postLikeMembers.contains(loginMember)) {
			count++;
		}

		return count;
	}

	@Transactional
	public void likePost(Long postId) {
		final Post post = getPostWithMember(postId);
		final Member loginMember = authUtil.getLoginMember();

		if (postLikeRepository.findByMemberAndPost(loginMember, post).isPresent()) {
			throw new EntityAlreadyExistException(POST_LIKE_ALREADY_EXIST);
		}

		postLikeRepository.save(new PostLike(loginMember, post));
		alarmService.alert(LIKE_POST, post.getMember(), post);
	}

	@Transactional
	public void unlikePost(Long postId) {
		final Post post = getPostWithMember(postId);
		final Member loginMember = authUtil.getLoginMember();
		postLikeRepository.delete(getPostLike(loginMember, post));
		alarmService.delete(LIKE_POST, post.getMember(), post);
	}

	public Page<LikeMemberDto> getPostLikeMembersDtoPage(Long postId, int page, int size) {
		final Post post = getPost(postId);
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(page, size);

		Page<LikeMemberDto> likeMemberDtoPage;
		if (post.getMember().equals(loginMember) || post.isLikeFlag()) {
			likeMemberDtoPage = postLikeRepository.findPostLikeMembersDtoPage(pageable, postId, loginMember.getId());
		} else {
			likeMemberDtoPage = postLikeRepository.findPostLikeMembersDtoPageOfFollowingsByMemberIdAndPostId(pageable,
				loginMember.getId(), postId);
		}

		if (postLikeRepository.findByMemberAndPost(loginMember, post).isPresent()) {
			final List<LikeMemberDto> likeMemberDtos = new ArrayList<>();
			likeMemberDtos.add(new LikeMemberDto(loginMember, false, false));
			likeMemberDtos.addAll(likeMemberDtoPage.getContent());
			final int countByMe = 1;
			final long total = likeMemberDtoPage.getTotalElements() + countByMe;
			likeMemberDtoPage = new PageImpl<>(likeMemberDtos, pageable, total);
		}
		final List<MemberDto> memberDtos = likeMemberDtoPage.getContent().stream()
			.map(LikeMemberDto::getMember)
			.collect(toList());
		memberStoryService.setHasStoryInMemberDtos(memberDtos);

		return likeMemberDtoPage;
	}

	@Transactional
	public void bookmark(Long postId) {
		final Post post = getPost(postId);
		final Member loginMember = authUtil.getLoginMember();

		if (bookmarkRepository.findByMemberAndPost(loginMember, post).isPresent()) {
			throw new EntityAlreadyExistException(BOOKMARK_ALREADY_EXIST);
		}

		bookmarkRepository.save(new Bookmark(loginMember, post));
	}

	@Transactional
	public void unBookmark(Long postId) {
		bookmarkRepository.delete(getBookmark(authUtil.getLoginMember(), getPost(postId)));
	}

	@Transactional
	public void sharePost(Long postId, List<String> usernames) {
		messageService.sendMessageToMembersIndividually(authUtil.getLoginMember(), getPost(postId), usernames);
	}

	// TODO: 리팩토링 post + hashtagPost 쿼리 최적화
	public Page<PostDto> getHashTagPosts(int page, int size, String name) {
		final Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "postId");
		return hashtagRepository.findByName(name)
			.map(hashtag -> {
				final List<Long> postIds = hashtagPostRepository.findAllWithPostByHashtagId(pageable, hashtag.getId())
					.stream()
					.map(hashtagPost -> hashtagPost.getPost().getId())
					.collect(toList());

				return getPostDtoPage(pageable, postIds);
			})
			.orElse(new PageImpl<>(new ArrayList<>(), pageable, NONE)); // TODO: throw exception, ApiResponse 업데이트
	}

	private Page<PostDto> getPostDtoPage(Pageable pageable, List<Long> postIds) {
		final Member loginMember = authUtil.getLoginMember();
		final Page<PostDto> postDtoPage = postRepository.findPostDtoPageByMemberIdAndPostIdIn(pageable,
			loginMember.getId(), postIds);
		setContents(loginMember, postDtoPage.getContent());
		return postDtoPage;
	}

	private void setContents(Member loginMember, List<PostDto> postDtos) {
		final List<Long> postIds = postDtos.stream()
			.map(PostDto::getPostId)
			.collect(toList());
		final List<MemberDto> memberDtos = postDtos.stream()
			.map(PostDto::getMember)
			.collect(toList());

		memberStoryService.setHasStoryInMemberDtos(memberDtos);
		setPostImages(postDtos, postIds);
		setRecentComments(loginMember.getId(), postDtos, postIds);
		setFollowingMemberUsernameLikedPost(loginMember, postDtos, postIds);
		setMentionAndHashtagList(postDtos);
		hidePostLikesCountIfPostLikeFlagIsFalse(loginMember, postDtos);
	}

	private void setContent(Member loginMember, PostDto postDto) {
		memberStoryService.setHasStoryInMemberDtos(List.of(postDto.getMember()));
		setPostImages(List.of(postDto), List.of(postDto.getPostId()));
		setFollowingMemberUsernameLikedPost(loginMember, List.of(postDto), List.of(postDto.getPostId()));
		setComments(postDto);
		hidePostLikesCountIfPostLikeFlagIsFalse(loginMember, postDto);
		setMentionAndHashtagList(postDto);
	}

	private void setContentWithoutLogin(PostDto postDto) {
		memberStoryService.setHasStoryInMemberDtos(List.of(postDto.getMember()));
		setPostImages(List.of(postDto), List.of(postDto.getPostId()));
		setComments(postDto);
		setMentionAndHashtagList(postDto);
		if (postDto.isPostLikeFlag()) {
			postDto.setPostLikesCount(0);
		}
	}

	private void setMentionAndHashtagList(PostDto postDto) {
		setMentionAndHashtagList(List.of(postDto));
	}

	private void setMentionAndHashtagList(List<PostDto> postDtos) {
		final List<String> mentionedUsernames = new ArrayList<>();
		postDtos.forEach(postDto -> mentionedUsernames.addAll(
			stringExtractUtil.extractMentionsWithExceptList(postDto.getPostContent(), mentionedUsernames)));
		final List<String> existentUsernames = memberRepository.findAllByUsernameIn(mentionedUsernames).stream()
			.map(Member::getUsername)
			.collect(toList());

		postDtos.forEach(postDto -> {
			final List<String> mentionsOfContent = stringExtractUtil.extractMentions(postDto.getPostContent()).stream()
				.filter(existentUsernames::contains)
				.collect(toList());
			postDto.setMentionsOfContent(mentionsOfContent);

			final List<String> hashtagsOfContent = stringExtractUtil.extractHashtags(postDto.getPostContent());
			postDto.setHashtagsOfContent(hashtagsOfContent);
		});
	}

	private void validateParameters(int multipartFileSize, int altTextSize, List<PostImageTagRequest> tags) {
		final List<FieldError> errors = new ArrayList<>();

		if (multipartFileSize != altTextSize) {
			errors.add(new FieldError("postImages.size" + COMMA_WITH_BLANK + "altTexts.size",
				multipartFileSize + COMMA_WITH_BLANK + altTextSize, POST_IMAGES_AND_ALT_TEXTS_MISMATCH.getMessage()));
		}

		final List<String> usernames = tags.stream()
			.map(PostImageTagRequest::getUsername)
			.collect(toList());
		final Map<String, Member> usernameMap = memberRepository.findAllByUsernameIn(usernames).stream()
			.collect(toMap(Member::getUsername, m -> m));

		for (int i = 0; i < usernames.size(); i++) {
			final String username = usernames.get(i);
			if (!usernameMap.containsKey(username)) {
				final String field = String.format("postImageTags[%s].username", i);
				errors.add(new FieldError(field, username, MEMBER_NOT_FOUND.getMessage()));
			}
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
	}

	private void hidePostLikesCountIfPostLikeFlagIsFalse(Member loginMember, PostDto postDto) {
		hidePostLikesCountIfPostLikeFlagIsFalse(loginMember, List.of(postDto));
	}

	private void hidePostLikesCountIfPostLikeFlagIsFalse(Member loginMember, List<PostDto> postDtos) {
		final Map<Long, PostDto> postDtosToHidePostLikesCountMap = postDtos.stream()
			.filter(postDto -> !postDto.getMember().getId().equals(loginMember.getId()) && !postDto.isLikeOptionFlag())
			.collect(toMap(PostDto::getPostId, PostDto -> PostDto));

		final List<Long> postIds = new ArrayList<>(postDtosToHidePostLikesCountMap.keySet());
		final List<PostLikeCountDto> postLikeCountDtos = postLikeRepository
			.findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn(loginMember, postIds);

		postLikeCountDtos.forEach(postLikeCountDto -> postDtosToHidePostLikesCountMap.get(postLikeCountDto.getPostId())
			.setPostLikesCount(postLikeCountDto.getPostLikesCount()));
	}

	private void setPostImages(List<PostDto> postDtos, List<Long> postIds) {
		final List<PostImageDto> postImageDtos = postImageRepository.findAllPostImageDtoByPostIdIn(postIds);
		final List<Long> postImageIds = postImageDtos.stream()
			.map(PostImageDto::getId)
			.collect(toList());

		setPostTags(postImageDtos, postImageIds);

		final Map<Long, List<PostImageDto>> postDtoMap = postImageDtos.stream()
			.collect(groupingBy(PostImageDto::getPostId));
		postDtos.forEach(p -> p.setPostImages(postDtoMap.get(p.getPostId())));
	}

	private void setPostTags(List<PostImageDto> postImageDtos, List<Long> postImageIds) {
		final List<PostTagDto> postTagDtos = postTagRepository.findAllPostTagDto(postImageIds);

		final Map<Long, List<PostTagDto>> postImageDtoMap = postTagDtos.stream()
			.collect(groupingBy(PostTagDto::getPostImageId));
		postImageDtos.forEach(i -> i.setPostTags(postImageDtoMap.get(i.getId())));
	}

	private void setFollowingMemberUsernameLikedPost(Member member, List<PostDto> postDtos, List<Long> postIds) {
		final Map<Long, List<PostLikeDto>> postLikeDtoMap =
			postLikeRepository.findAllPostLikeDtoOfFollowingsByMemberIdAndPostIdIn(member.getId(), postIds)
				.stream()
				.collect(groupingBy(PostLikeDto::getPostId));
		postDtos.forEach(postDto ->
			postDto.setFollowingMemberUsernameLikedPost(postLikeDtoMap.containsKey(postDto.getPostId())
				? postLikeDtoMap.get(postDto.getPostId()).get(ANY_INDEX).getUsername() : EMPTY));
	}

	private void setRecentComments(Long memberId, List<PostDto> postDtos, List<Long> postIds) {
		final Map<Long, List<CommentDto>> recentCommentMap =
			commentRepository.findAllRecentCommentDtoByMemberIdAndPostIdIn(memberId, postIds).stream()
				.collect(groupingBy(CommentDto::getPostId));

		final List<CommentDto> totalCommentDtos = new ArrayList<>();
		postDtos.forEach(postDto -> {
			if (recentCommentMap.containsKey(postDto.getPostId())) {
				final List<CommentDto> commentDtos = recentCommentMap.get(postDto.getPostId());
				totalCommentDtos.addAll(commentDtos);
				postDto.setRecentComments(commentDtos);
			}
		});
		commentService.setMentionAndHashtagList(totalCommentDtos);

		final List<MemberDto> totalMemberDtos = totalCommentDtos.stream()
			.map(CommentDto::getMember)
			.collect(toList());
		memberStoryService.setHasStoryInMemberDtos(totalMemberDtos);
	}

	private void setComments(PostDto postDto) {
		final List<CommentDto> commentDtos = commentService.getCommentDtoPageWithoutLogin(postDto.getPostId(),
			BASE_PAGE_NUMBER).getContent();
		commentService.setHasStory(commentDtos);
		commentService.setMentionAndHashtagList(commentDtos);
		postDto.setRecentComments(commentDtos);
	}

	private Post getPost(Long postId) {
		return postRepository.findById(postId)
			.orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
	}

	private PostLike getPostLike(Member member, Post post) {
		return postLikeRepository.findByMemberAndPost(member, post)
			.orElseThrow(() -> new EntityNotFoundException(POST_LIKE_NOT_FOUND));
	}

	private Post getPostWithMember(Long postId) {
		return postRepository.findWithMemberById(postId)
			.orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
	}

	private Bookmark getBookmark(Member member, Post post) {
		return bookmarkRepository.findByMemberAndPost(member, post)
			.orElseThrow(() -> new EntityNotFoundException(BOOKMARK_NOT_FOUND));
	}

}
