package cloneproject.Instagram.domain.feed.service;

import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.dm.service.MessageService;
import cloneproject.Instagram.domain.feed.dto.*;
import cloneproject.Instagram.domain.feed.entity.*;
import cloneproject.Instagram.domain.feed.exception.CantDeletePostException;
import cloneproject.Instagram.domain.feed.repository.*;
import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import cloneproject.Instagram.domain.hashtag.repository.HashtagPostRepository;
import cloneproject.Instagram.domain.hashtag.repository.HashtagRepository;
import cloneproject.Instagram.domain.hashtag.service.HashtagService;
import cloneproject.Instagram.domain.member.dto.LikeMembersDTO;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.mention.service.MentionService;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.global.error.ErrorResponse.FieldError;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.error.exception.InvalidInputException;
import cloneproject.Instagram.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;
import static cloneproject.Instagram.global.error.ErrorCode.*;

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
	private final MemberStoryRedisRepository memberStoryRedisRepository;

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
			.build();

		postRepository.save(post);
		postImageService.saveAll(post, request.getPostImages(), request.getAltTexts(), request.getPostImageTags());
		hashtagService.registerHashtags(post);
		mentionService.mentionMembers(loginMember, post);

		final Set<String> taggedMemberUsernames = request.getPostImageTags().stream()
			.map(PostImageTagRequest::getUsername)
			.collect(Collectors.toSet());
		taggedMemberUsernames.remove(loginMember.getUsername());
		messageService.sendMessageToMembersIndividually(loginMember, post, taggedMemberUsernames);

		return new PostUploadResponse(post.getId());
	}

	private void validateParameters(int multipartFileSize, int altTextSize, List<PostImageTagRequest> tags) {
		final List<FieldError> errors = new ArrayList<>();

		if (multipartFileSize != altTextSize) {
			errors.add(new FieldError("postImages.size, altTexts.size", multipartFileSize + ", " + altTextSize,
				POST_IMAGES_AND_ALT_TEXTS_MISMATCH.getMessage()));
		}

		final List<String> usernames = tags.stream()
			.map(PostImageTagRequest::getUsername)
			.collect(Collectors.toList());
		final Map<String, Member> usernameMap = memberRepository.findAllByUsernameIn(usernames).stream()
			.collect(Collectors.toMap(Member::getUsername, m -> m));

		for (int i = 0; i < usernames.size(); i++) {
			final String username = usernames.get(i);

			if (!usernameMap.containsKey(username)) {
				errors.add(
					new FieldError("postImageTags[" + i + "].username", username, MEMBER_NOT_FOUND.getMessage()));
			}
		}

		if (!errors.isEmpty()) {
			throw new InvalidInputException(errors);
		}
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

	public Page<PostDTO> getPostDtoPage(int size, int page) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1) + 10;
		final Pageable pageable = PageRequest.of(page, size);
		final Page<PostDTO> postDtoPage = postRepository.findPostDtoPage(loginMember.getId(), pageable);
		setContent(loginMember, postDtoPage.getContent());

		return postDtoPage;
	}

	public Page<PostDTO> getPostDtoPage(Pageable pageable, List<Long> postIds) {
		final Member loginMember = authUtil.getLoginMember();
		final Page<PostDTO> postDtoPage = postRepository.findPostDtoPage(pageable, loginMember.getId(), postIds);
		setContent(loginMember, postDtoPage.getContent());

		return postDtoPage;
	}

	public List<PostDTO> getRecent10PostDTOs() {
		final Member loginMember = authUtil.getLoginMember();
		final Pageable pageable = PageRequest.of(0, 10);
		final Page<PostDTO> postDtoPage = postRepository.findPostDtoPage(loginMember.getId(), pageable);
		setContent(loginMember, postDtoPage.getContent());

		return postDtoPage.getContent();
	}

	private void setContent(Member loginMember, List<PostDTO> postDTOs) {
		final List<Long> postIds = postDTOs.stream()
			.map(PostDTO::getPostId)
			.collect(Collectors.toList());

		setHashStory(postDTOs);
		setPostImages(postDTOs, postIds);
		setRecentComments(loginMember.getId(), postDTOs, postIds);
		setFollowingMemberUsernameLikedPost(loginMember.getId(), postDTOs, postIds);
	}

	private void setPostImages(List<PostDTO> postDTOs, List<Long> postIds) {
		final List<PostImageDTO> postImageDTOs = postImageRepository.findAllPostImageDto(postIds);
		final List<Long> postImageIds = postImageDTOs.stream()
			.map(PostImageDTO::getId)
			.collect(Collectors.toList());

		setPostTags(postImageDTOs, postImageIds);

		final Map<Long, List<PostImageDTO>> postDTOMap = postImageDTOs.stream()
			.collect(Collectors.groupingBy(PostImageDTO::getPostId));
		postDTOs.forEach(p -> p.setPostImageDTOs(postDTOMap.get(p.getPostId())));
	}

	private void setPostTags(List<PostImageDTO> postImageDTOs, List<Long> postImageIds) {
		final List<PostTagDTO> postTagDTOs = postTagRepository.findAllPostTagDto(postImageIds);

		final Map<Long, List<PostTagDTO>> postImageDTOMap = postTagDTOs.stream()
			.collect(Collectors.groupingBy(PostTagDTO::getPostImageId));
		postImageDTOs.forEach(i -> i.setPostTagDTOs(postImageDTOMap.get(i.getId())));
	}

	private void setFollowingMemberUsernameLikedPost(Long memberId, List<PostDTO> postDTOs, List<Long> postIds) {
		final Map<Long, List<PostLikeDTO>> postLikeDTOMap =
			postLikeRepository.findAllPostLikeDtoOfFollowings(memberId, postIds).stream()
				.collect(Collectors.groupingBy(PostLikeDTO::getPostId));
		postDTOs.forEach(p -> p.setFollowingMemberUsernameLikedPost(
			postLikeDTOMap.containsKey(p.getPostId()) ? postLikeDTOMap.get(p.getPostId()).get(0).getUsername() : ""));
	}

	private void setHashStory(List<PostDTO> postDTOs) {
		postDTOs.forEach(post -> {
			final MemberDTO postMember = post.getMember();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(postMember.getId()).size() > 0;
			postMember.setHasStory(hasStory);
		});
	}

	private void setRecentComments(Long memberId, List<PostDTO> postDTOs, List<Long> postIds) {
		final Map<Long, List<CommentDTO>> recentCommentMap =
			commentRepository.findAllRecentCommentDto(memberId, postIds).stream()
				.collect(Collectors.groupingBy(CommentDTO::getPostId));
		postDTOs.forEach(p -> p.setRecentComments(recentCommentMap.get(p.getPostId())));
	}

	public PostResponse getPostResponse(Long postId) {
		final Member loginMember = authUtil.getLoginMember();
		final PostResponse postResponse = postRepository.findPostResponse(postId, loginMember.getId())
			.orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));

		setHashStory(postResponse);
		setPostImages(postResponse);
		setRecentComments(loginMember.getId(), postResponse);
		setFollowingMemberUsernameLikedPost(loginMember.getId(), postResponse);

		return postResponse;
	}

	private void setRecentComments(Long memberId, PostResponse postResponse) {
		final Pageable pageable = PageRequest.of(0, 10);
		final List<CommentDTO> commentDTOs = commentRepository.findCommentDtoPage(memberId, postResponse.getPostId(),
			pageable).getContent();

		setHasStory(commentDTOs);
		postResponse.setCommentDTOs(commentDTOs);
	}

	private void setHasStory(List<CommentDTO> commentDTOs) {
		commentDTOs.forEach(comment -> {
			final MemberDTO member = comment.getMember();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
			member.setHasStory(hasStory);
		});
	}

	private void setPostImages(PostResponse postResponse) {
		final List<PostImageDTO> postImageDTOs = postImageRepository.findAllPostImageDto(
			List.of(postResponse.getPostId()));

		setPostTags(postResponse);
		postResponse.setPostImageDTOs(postImageDTOs);
	}

	private void setPostTags(PostResponse postResponse) {
		final List<PostTagDTO> postTagDTOs = postTagRepository.findAllPostTagDto(List.of(postResponse.getPostId()));

		final Map<Long, List<PostTagDTO>> postImageDTOMap = postTagDTOs.stream()
			.collect(Collectors.groupingBy(PostTagDTO::getPostImageId));
		postResponse.getPostImageDTOs().forEach(i -> i.setPostTagDTOs(postImageDTOMap.get(i.getId())));
	}

	private void setFollowingMemberUsernameLikedPost(Long memberId, PostResponse postResponse) {
		final List<PostLikeDTO> postLikeDTOs = postLikeRepository.findAllPostLikeDtoOfFollowings(memberId,
			List.of(postResponse.getPostId()));
		postResponse.setFollowingMemberUsernameLikedPost(
			postLikeDTOs.isEmpty() ? "" : postLikeDTOs.get(0).getUsername());
	}

	private void setHashStory(PostResponse postResponse) {
		final MemberDTO postMember = postResponse.getMember();
		final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(postMember.getId()).size() > 0;
		postMember.setHasStory(hasStory);
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
		final PostLike postLike = getPostLike(loginMember, post);
		postLikeRepository.delete(postLike);
		alarmService.delete(LIKE_POST, post.getMember(), post);
	}

	public Page<LikeMembersDTO> getPostLikeMembersDtoPage(Long postId, int page, int size) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, size);
		final Page<LikeMembersDTO> likeMembersDTOs =
			postLikeRepository.findPostLikeMembersDtoPage(pageable, postId, loginMember.getId());

		setHasStory(likeMembersDTOs);

		return likeMembersDTOs;
	}

	private void setHasStory(Page<LikeMembersDTO> likeMembersDTOs) {
		likeMembersDTOs.getContent()
			.forEach(dto -> dto.setHasStory(
				memberStoryRedisRepository.findAllByMemberId(dto.getMember().getId()).size() > 0));
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
		final Post post = getPost(postId);
		final Member loginMember = authUtil.getLoginMember();
		final Bookmark bookmark = getBookmark(loginMember, post);
		bookmarkRepository.delete(bookmark);
	}

	@Transactional
	public void sharePost(Long postId, List<String> usernames) {
		final Post post = getPost(postId);
		final Member loginMember = authUtil.getLoginMember();

		messageService.sendMessageToMembersIndividually(loginMember, post, usernames);
	}

	public Page<PostDTO> getHashTagPosts(int page, int size, String name) {
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, size);
		final Optional<Hashtag> findHashtag = hashtagRepository.findByName(name);

		if (findHashtag.isEmpty()) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0L);
		}

		final Hashtag hashtag = findHashtag.get();
		final List<Long> postIds =
			hashtagPostRepository.findAllByHashtagOrderByPostIdDesc(pageable, hashtag).getContent().stream()
				.map(hashtagPost -> hashtagPost.getPost().getId())
				.collect(Collectors.toList());

		return getPostDtoPage(pageable, postIds);
	}

	private Post getPost(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
	}

	private PostLike getPostLike(Member member, Post post) {
		return postLikeRepository.findByMemberAndPost(member, post)
			.orElseThrow(() -> new EntityNotFoundException(POST_LIKE_NOT_FOUND));
	}

	private Post getPostWithMember(Long postId) {
		return postRepository.findWithMemberById(postId).orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
	}

	private Bookmark getBookmark(Member member, Post post) {
		return bookmarkRepository.findByMemberAndPost(member, post)
			.orElseThrow(() -> new EntityNotFoundException(BOOKMARK_NOT_FOUND));
	}

}
