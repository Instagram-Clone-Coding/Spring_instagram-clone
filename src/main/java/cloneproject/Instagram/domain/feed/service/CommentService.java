package cloneproject.Instagram.domain.feed.service;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;
import static cloneproject.Instagram.global.error.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.alarm.service.AlarmService;
import cloneproject.Instagram.domain.feed.dto.CommentDto;
import cloneproject.Instagram.domain.feed.dto.CommentUploadRequest;
import cloneproject.Instagram.domain.feed.dto.CommentUploadResponse;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.CommentLike;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.exception.CantDeleteCommentException;
import cloneproject.Instagram.domain.feed.exception.CantUploadCommentException;
import cloneproject.Instagram.domain.feed.exception.CantUploadReplyException;
import cloneproject.Instagram.domain.feed.repository.CommentLikeRepository;
import cloneproject.Instagram.domain.feed.repository.CommentRepository;
import cloneproject.Instagram.domain.feed.repository.PostLikeRepository;
import cloneproject.Instagram.domain.feed.repository.PostRepository;
import cloneproject.Instagram.domain.hashtag.service.HashtagService;
import cloneproject.Instagram.domain.member.dto.LikeMemberDto;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import cloneproject.Instagram.domain.mention.service.MentionService;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;
import cloneproject.Instagram.global.error.exception.EntityAlreadyExistException;
import cloneproject.Instagram.global.error.exception.EntityNotFoundException;
import cloneproject.Instagram.global.util.AuthUtil;
import cloneproject.Instagram.global.util.StringExtractUtil;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

	private final AuthUtil authUtil;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final AlarmService alarmService;
	private final HashtagService hashtagService;
	private final CommentLikeService commentLikeService;
	private final RecentCommentService recentCommentService;
	private final MentionService mentionService;
	private final MemberStoryRedisRepository memberStoryRedisRepository;
	private final StringExtractUtil stringExtractUtil;
	private final MemberRepository memberRepository;

	@Transactional
	public CommentUploadResponse uploadComment(CommentUploadRequest request) {
		final Post post = getPostWithMember(request.getPostId());

		if (!post.isCommentFlag()) {
			throw new CantUploadCommentException();
		}

		final Member loginMember = authUtil.getLoginMember();
		final Optional<Comment> parent = commentRepository.findById(request.getParentId());
		final boolean isRootComment = parent.isEmpty();
		final Comment comment = commentRepository.save(
			new Comment(isRootComment ? null : parent.get(), loginMember, post, request.getContent()));

		if (isRootComment) {
			recentCommentService.updateByUploadingComment(post, loginMember, comment);
		} else if (parent.get().getParent() != null) {
			throw new CantUploadReplyException();
		}

		hashtagService.registerHashtags(post, comment);
		mentionService.mentionMembers(loginMember, comment);
		alarmService.alert(COMMENT, post.getMember(), post, comment);

		return new CommentUploadResponse(comment.getId());
	}

	@Transactional
	public void deleteComment(Long commentId) {
		final Member loginMember = authUtil.getLoginMember();
		final Comment comment = getCommentWithPostAndMember(commentId);

		if (!comment.getMember().getId().equals(loginMember.getId())) {
			throw new CantDeleteCommentException();
		}

		final boolean isRootComment = comment.getParent() == null;
		final List<Comment> comments = new ArrayList<>();
		comments.add(comment);

		if (isRootComment) {
			comments.addAll(commentRepository.findAllByParent(comment));
		}

		alarmService.deleteAll(comments);
		hashtagService.unregisterHashtagsByDeletingComments(comment.getPost(), comments);
		mentionService.deleteAll(comments);
		commentLikeService.deleteAll(comments);
		commentRepository.deleteAll(comments.subList(1, comments.size()));

		if (isRootComment) {
			recentCommentService.updateByDeletingComment(loginMember, comment.getPost(), comment);
		}

		commentRepository.delete(comment);
	}

	public Page<CommentDto> getCommentDtoPage(Long postId, int page) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, 10);

		final Page<CommentDto> commentDtoPage = commentRepository.findCommentDtoPage(loginMember.getId(), postId,
			pageable);
		final List<CommentDto> content = commentDtoPage.getContent();
		setHasStory(content);
		setMentionAndHashtagList(content);

		return commentDtoPage;
	}

	public Page<CommentDto> getCommentDtoPageWithoutLogin(Long postId, int page) {
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, 10);

		final Page<CommentDto> commentDtoPage = commentRepository.findCommentDtoPageWithoutLogin( postId, pageable);
		final List<CommentDto> content = commentDtoPage.getContent();
		setHasStory(content);
		setMentionAndHashtagList(content);

		return commentDtoPage;
	}

	public Page<CommentDto> getReplyDtoPage(Long commentId, int page) {
		final Member loginMember = authUtil.getLoginMember();
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, 10);

		final Page<CommentDto> replyDtoPage = commentRepository.findReplyDtoPage(loginMember.getId(), commentId,
			pageable);
		final List<CommentDto> content = replyDtoPage.getContent();
		setHasStory(content);
		setMentionAndHashtagList(content);

		return replyDtoPage;
	}

	@Transactional
	public void likeComment(Long commentId) {
		final Comment comment = getCommentWithPostAndMember(commentId);
		final Member loginMember = authUtil.getLoginMember();

		if (commentLikeRepository.findByMemberAndComment(loginMember, comment).isPresent())
			throw new EntityAlreadyExistException(COMMENT_LIKE_ALREADY_EXIST);

		commentLikeRepository.save(new CommentLike(loginMember, comment));
		alarmService.alert(LIKE_COMMENT, comment.getMember(), comment.getPost(), comment);
	}

	@Transactional
	public void unlikeComment(Long commentId) {
		final Comment comment = getCommentWithMember(commentId);
		final Member loginMember = authUtil.getLoginMember();
		final CommentLike commentLike = getCommentLike(loginMember, comment);
		commentLikeRepository.delete(commentLike);
		alarmService.delete(LIKE_COMMENT, comment.getMember(), comment);
	}

	public Page<LikeMemberDto> getCommentLikeMembersDtoPage(Long commentId, int page, int size) {
		final Member loginMember = authUtil.getLoginMember();
		final Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND));
		page = (page == 0 ? 0 : page - 1);
		final Pageable pageable = PageRequest.of(page, size);

		Page<LikeMemberDto> likeMemberDtoPage =
			postLikeRepository.findCommentLikeMembersDtoPage(pageable, commentId, loginMember.getId());

		if (commentLikeRepository.findByMemberAndComment(loginMember, comment).isPresent()) {
			final List<LikeMemberDto> likeMemberDtos = new ArrayList<>();
			likeMemberDtos.add(new LikeMemberDto(loginMember, false, false));
			likeMemberDtos.addAll(likeMemberDtoPage.getContent());
			likeMemberDtoPage = new PageImpl<>(likeMemberDtos, pageable, likeMemberDtoPage.getTotalElements() + 1);
		}
		setHasStory(likeMemberDtoPage);

		return likeMemberDtoPage;
	}

	@Transactional
	public void deleteAllInPost(Post post) {
		final List<Comment> comments = commentRepository.findAllByPost(post);
		commentLikeService.deleteAll(comments);
		recentCommentService.deleteAll(post);
	}

	public void setMentionAndHashtagList(List<CommentDto> content) {
		content.forEach(comment -> {
			final List<String> mentionedUsernames = stringExtractUtil.extractMentionsWithExceptList(comment.getContent(), List.of());
			final List<String> existentUsernames = memberRepository.findAllByUsernameIn(mentionedUsernames).stream()
				.map(Member::getUsername)
				.collect(Collectors.toList());
			comment.setMentionsOfContent(existentUsernames);

			final List<String> hashtags = stringExtractUtil.extractHashtags(comment.getContent());
			comment.setHashtagsOfContent(hashtags);
		});
	}

	public void setHasStory(List<CommentDto> commentDtos) {
		commentDtos.forEach(comment -> {
			final MemberDto member = comment.getMember();
			final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(member.getId()).size() > 0;
			member.setHasStory(hasStory);
		});
	}

	private void setHasStory(Page<LikeMemberDto> likeMembersDTOs) {
		likeMembersDTOs.getContent()
			.forEach(dto -> dto.setHasStory(
				memberStoryRedisRepository.findAllByMemberId(dto.getMember().getId()).size() > 0));
	}

	private Post getPostWithMember(Long postId) {
		return postRepository.findWithMemberById(postId).orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND));
	}

	private Comment getCommentWithPostAndMember(Long commentId) {
		return commentRepository.findWithPostAndMemberById(commentId)
			.orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND));
	}

	private Comment getCommentWithMember(Long commentId) {
		return commentRepository.findWithMemberById(commentId)
			.orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND));
	}

	private CommentLike getCommentLike(Member member, Comment comment) {
		return commentLikeRepository.findByMemberAndComment(member, comment)
			.orElseThrow(() -> new EntityNotFoundException(COMMENT_LIKE_NOT_FOUND));
	}

}
