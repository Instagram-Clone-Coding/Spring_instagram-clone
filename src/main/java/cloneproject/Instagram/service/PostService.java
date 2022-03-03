package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.StatusResponse;
import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.dto.chat.MessageAction;
import cloneproject.Instagram.dto.chat.MessageDTO;
import cloneproject.Instagram.dto.chat.MessageResponse;
import cloneproject.Instagram.dto.comment.CommentCreateRequest;
import cloneproject.Instagram.dto.comment.CommentCreateResponse;
import cloneproject.Instagram.dto.comment.CommentDTO;
import cloneproject.Instagram.dto.error.ErrorResponse.FieldError;
import cloneproject.Instagram.dto.member.LikeMembersDTO;
import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.dto.post.PostResponse;
import cloneproject.Instagram.entity.alarms.Alarm;
import cloneproject.Instagram.entity.chat.*;
import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.comment.CommentLike;
import cloneproject.Instagram.entity.comment.RecentComment;
import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.mention.Mention;
import cloneproject.Instagram.entity.post.*;
import cloneproject.Instagram.exception.*;
import cloneproject.Instagram.repository.*;
import cloneproject.Instagram.repository.chat.*;
import cloneproject.Instagram.repository.post.CommentRepository;
import cloneproject.Instagram.repository.post.PostRepository;
import cloneproject.Instagram.repository.post.RecentCommentRepository;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.util.StringExtractUtil;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cloneproject.Instagram.dto.alarm.AlarmType.*;
import static cloneproject.Instagram.dto.error.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader uploader;
    private final AlarmService alarmService;
    private final CommentRepository commentRepository;
    private final RecentCommentRepository recentCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final StringExtractUtil stringExtractUtil;
    private final MentionRepository mentionRepository;
    private final AlarmRepository alarmRepository;
    private final PostTagRepository postTagRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessagePostRepository messagePostRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final HashtagRepository hashtagRepository;
    private final HashtagPostRepository hashtagPostRepository;

    public Page<PostDTO> getPostDtoPage(int size, int page) {
        page = (page == 0 ? 0 : page - 1) + 10;
        final Pageable pageable = PageRequest.of(page, size);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        return postRepository.findPostDtoPage(member, pageable);
    }

    @Transactional
    public void delete(Long postId) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        final Post post = postRepository.findByIdAndMemberId(postId, memberId).orElseThrow(PostNotFoundException::new);

        final List<Alarm> alarms = alarmRepository.findAllByPost(post);
        alarmRepository.deleteAllInBatch(alarms);

        unregisterHashtags(post);

        final List<Mention> mentions = mentionRepository.findAllByPost(post);
        mentionRepository.deleteAllInBatch(mentions);

        final List<PostLike> postLikes = postLikeRepository.findAllByPost(post);
        postLikeRepository.deleteAllInBatch(postLikes);

        final List<PostImage> postImages = postImageRepository.findAllByPost(post);
        postImages.forEach(pi -> uploader.deleteImage("post", pi.getImage()));
        final List<PostTag> postTags = postTagRepository.findAllByPostImageIn(postImages);
        postTagRepository.deleteAllInBatch(postTags);
        postImageRepository.deleteAllInBatch(postImages);

        final List<Comment> comments = commentRepository.findAllByPost(post);
        final List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentIn(comments);
        final List<RecentComment> recentComments = recentCommentRepository.findAllByPost(post);
        recentCommentRepository.deleteAllInBatch(recentComments);
        commentLikeRepository.deleteAllInBatch(commentLikes);
        commentRepository.deleteAllInBatch(comments);

        postRepository.delete(post);
    }

    public List<PostDTO> getRecent10PostDTOs() {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return postRepository.findRecent10PostDTOs(memberId);
    }

    public PostResponse getPost(Long postId) {
        final Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        return postRepository.findPostResponse(postId, memberId).orElseThrow(PostNotFoundException::new);
    }

    @Transactional
    public Long upload(String content, List<MultipartFile> postImages, List<String> altTexts, List<PostImageTagRequest> postImageTags, boolean commentFlag) {
        if (postImages.isEmpty())
            throw new PostImageInvalidException();
        if (postImages.size() != altTexts.size())
            throw new PostImagesAndAltTextsMismatchException();
        validatePostImageTags(postImages, postImageTags);

        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId)).orElseThrow(MemberDoesNotExistException::new);

        final Post post = Post.builder()
                .content(content)
                .member(member)
                .commentFlag(commentFlag)
                .build();
        postRepository.save(post);

        registerHashtags(post, content);

        final List<String> mentions = stringExtractUtil.extractMentions(content, List.of(member.getUsername()));
        final List<Member> mentionedMembers = memberRepository.findAllByUsernameIn(mentions);
        mentionRepository.savePostMentionsBatch(member.getId(), mentionedMembers, post.getId(), LocalDateTime.now());
        alarmService.alertBatch(MENTION_POST, mentionedMembers, post);

        final List<Image> images = postImages.stream()
                .map(pi -> uploader.uploadImage(pi, "post"))
                .collect(Collectors.toList());
        postRepository.savePostImages(images, post.getId(), altTexts);
        for (int i = 0; i < images.size(); i++) {
            post.getPostImages().add(new PostImage(post, images.get(i), altTexts.get(i)));
        }

        final List<Long> postImageIds = postImageRepository.findAllByPost(post).stream()
                .map(PostImage::getId)
                .collect(Collectors.toList());
        linkImageWithTags(postImageTags, postImageIds);
        postRepository.savePostTags(postImageTags);

        sendMessageToTaggedMembers(postImageTags, member, post);
        return post.getId();
    }

    private void sendMessageToTaggedMembers(List<PostImageTagRequest> postImageTags, Member inviter, Post post) {
        final Set<String> taggedMemberUsernames = postImageTags.stream()
                .map(PostImageTagRequest::getUsername)
                .collect(Collectors.toSet());
        taggedMemberUsernames.remove(inviter.getUsername());
        final List<Member> taggedMembers = memberRepository.findAllByUsernameIn(taggedMemberUsernames);
        final List<Member> members = new ArrayList<>();
        members.add(inviter);
        members.addAll(taggedMembers);

        final List<RoomMember> roomMembers = roomMemberRepository.findAllByMemberIn(members);
        final Map<Member, List<RoomMember>> roomMemberMapGroupByMember = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getMember));
        final Map<Room, List<RoomMember>> roomMemberMapGroupByRoom = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getRoom));

        taggedMembers.forEach(taggedMember -> {
            Room room = null;

            if (roomMemberMapGroupByMember.containsKey(taggedMember)) {
                for (RoomMember taggedRoomMember : roomMemberMapGroupByMember.get(taggedMember)) {
                    if (!roomMemberMapGroupByMember.containsKey(inviter))
                        continue;

                    for (RoomMember roomMember : roomMemberMapGroupByMember.get(inviter)) {
                        if (taggedRoomMember.getRoom().getId().equals(roomMember.getRoom().getId())
                                && roomMemberMapGroupByRoom.get(roomMember.getRoom()).size() == 2) {
                            room = roomMember.getRoom();
                            break;
                        }
                    }
                    if (room != null)
                        break;
                }
            }

            if (room == null) {
                room = roomRepository.save(new Room(inviter));
                roomMemberRepository.save(new RoomMember(inviter, room));
                roomMemberRepository.save(new RoomMember(taggedMember, room));
            }

            final MessagePost message = messagePostRepository.save(new MessagePost(post, inviter, room));
            roomUnreadMemberRepository.save(new RoomUnreadMember(room, message, taggedMember));

            final List<Member> privateRoomMembers = List.of(inviter, taggedMember);
            for (Member member : privateRoomMembers) {
                final Optional<JoinRoom> joinRoom = joinRoomRepository.findByMemberAndRoom(member, room);
                if (joinRoom.isPresent())
                    joinRoom.get().updateMessage(message);
                else
                    joinRoomRepository.save(new JoinRoom(room, member, message));
            }

            final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
            messagingTemplate.convertAndSend("/sub/" + taggedMember.getUsername(), response);
        });
    }

    private void linkImageWithTags(List<PostImageTagRequest> postImageTags, List<Long> postImageIds) {
        int idx = postImageTags.get(0).getId().intValue();
        for (PostImageTagRequest postImageTag : postImageTags) {
            if (idx != postImageTag.getId())
                idx = postImageTag.getId().intValue();
            postImageTag.setId(postImageIds.get(idx - 1));
        }
    }

    private void validatePostImageTags(List<MultipartFile> postImages, List<PostImageTagRequest> postImageTags) {
        if (!postImageTags.isEmpty()) {
            postImageTags.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
            final List<FieldError> errors = new ArrayList<>();
            final List<String> usernames = postImageTags.stream()
                    .map(PostImageTagRequest::getUsername)
                    .collect(Collectors.toList());
            final Map<String, Member> usernameMap = memberRepository.findAllByUsernameIn(usernames).stream()
                    .collect(Collectors.toMap(Member::getUsername, m -> m));

            for (PostImageTagRequest postImageTag : postImageTags) {
                final String username = postImageTag.getUsername();
                final long tagX = postImageTag.getTagX() == null ? -1L : postImageTag.getTagX();
                final long tagY = postImageTag.getTagY() == null ? -1L : postImageTag.getTagY();
                final long postImageId = postImageTag.getId() == null ? -1L : postImageTag.getId();

                if (!usernameMap.containsKey(postImageTag.getUsername()))
                    errors.add(new FieldError("username", username, MEMBER_DOES_NOT_EXIST.getMessage()));
                if (tagX < 0 || tagX > 100)
                    errors.add(new FieldError("tagX", Long.toString(tagX), INVALID_TAG_POSITION.getMessage()));
                if (tagY < 0 || tagY > 100)
                    errors.add(new FieldError("tagY", Long.toString(tagY), INVALID_TAG_POSITION.getMessage()));
                if (postImageId < 1 || postImageId > postImages.size())
                    errors.add(new FieldError("id", Long.toString(postImageId), INVALID_IMAGE_SEQUENCE.getMessage()));
                if (!errors.isEmpty())
                    throw new PostImageTagInvalidException(INVALID_INPUT_VALUE, errors);
            }
        }
    }

    @Transactional
    public boolean likePost(Long postId) {
        final Post post = postRepository.findWithMemberById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        if (postLikeRepository.findByMemberAndPost(member, post).isPresent())
            throw new PostLikeAlreadyExistException();
        postLikeRepository.save(new PostLike(member, post));
        alarmService.alert(LIKE_POST, post.getMember(), post);
        return true;
    }

    @Transactional
    public boolean likeComment(Long commentId) {
        final Comment comment = commentRepository.findWithPostAndMemberById(commentId).orElseThrow(CommentNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        if (commentLikeRepository.findByMemberAndComment(member, comment).isPresent())
            throw new CommentLikeAlreadyExistException();
        commentLikeRepository.save(new CommentLike(member, comment));
        alarmService.alert(LIKE_COMMENT, comment.getMember(), comment.getPost(), comment);
        return true;
    }

    @Transactional
    public boolean unlikePost(Long postId) {
        final Post post = postRepository.findWithMemberById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final PostLike postLike = postLikeRepository.findByMemberAndPost(member, post).orElseThrow(PostLikeNotFoundException::new);
        postLikeRepository.delete(postLike);
        alarmService.delete(LIKE_POST, post.getMember(), post);
        return true;
    }

    @Transactional
    public boolean unlikeComment(Long commentId) {
        final Comment comment = commentRepository.findWithMemberById(commentId).orElseThrow(CommentNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final CommentLike commentLike = commentLikeRepository.findByMemberAndComment(member, comment).orElseThrow(CommentLikeNotFoundException::new);
        commentLikeRepository.delete(commentLike);
        alarmService.delete(LIKE_COMMENT, comment.getMember(), comment);
        return true;
    }

    @Transactional
    public boolean savePost(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        if (bookmarkRepository.findByMemberAndPost(member, post).isPresent())
            throw new BookmarkAlreadyExistException();
        bookmarkRepository.save(new Bookmark(member, post));
        return true;
    }

    @Transactional
    public boolean unsavePost(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Bookmark bookmark = bookmarkRepository.findByMemberAndPost(member, post).orElseThrow(BookmarkNotFoundException::new);
        bookmarkRepository.delete(bookmark);
        return true;
    }

    @Transactional
    public CommentCreateResponse createComment(CommentCreateRequest request) {
        final Post post = postRepository.findWithMemberById(request.getPostId()).orElseThrow(PostNotFoundException::new);
        if (!post.isCommentFlag())
            throw new CantCreateCommentException();

        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Optional<Comment> parent = commentRepository.findById(request.getParentId());
        final Comment comment = commentRepository.save(new Comment(parent.isEmpty() ? null : parent.get(), member, post, request.getContent()));

        registerHashtags(post, request.getContent());

        final List<String> mentions = stringExtractUtil.extractMentions(comment.getContent(), List.of(member.getUsername(), post.getMember().getUsername()));
        final List<Member> mentionedMembers = memberRepository.findAllByUsernameIn(mentions);
        mentionRepository.saveCommentMentionsBatch(member.getId(), mentionedMembers, post.getId(), comment.getId(), LocalDateTime.now());
        alarmService.alertBatch(MENTION_COMMENT, mentionedMembers, post, comment);

        final List<RecentComment> recentComments = recentCommentRepository.findAllByPost(post);
        if (recentComments.size() == 2) {
            final RecentComment recentComment = recentComments.get(0).getId() < recentComments.get(1).getId() ? recentComments.get(0) : recentComments.get(1);
            recentCommentRepository.delete(recentComment);
        }
        recentCommentRepository.save(new RecentComment(member, post, comment));
        alarmService.alert(COMMENT, post.getMember(), post, comment);

        return new CommentCreateResponse(comment.getId());
    }

    @Transactional
    public StatusResponse deleteComment(Long commentId) {
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Comment comment = commentRepository.findWithPostAndMemberById(commentId).orElseThrow(CommentNotFoundException::new);
        if (!comment.getMember().getId().equals(memberId))
            throw new CommentCantDeleteException();

        final List<RecentComment> recentComments = recentCommentRepository.findAllWithCommentByPostId(comment.getPost().getId());
        for (RecentComment recentComment : recentComments) {
            if (recentComment.getComment().equals(comment)) {
                recentCommentRepository.delete(recentComment);
                break;
            }
        }

        List<AlarmType> alarmTypes = List.of(MENTION_COMMENT, LIKE_COMMENT, COMMENT);
        final List<Alarm> alarms = alarmRepository.findAllByCommentAndTypeIn(comment, alarmTypes);
        alarmRepository.deleteAllInBatch(alarms);

        unregisterHashtags(comment.getPost());

        final List<Mention> mentions = mentionRepository.findAllByComment(comment);
        mentionRepository.deleteAllInBatch(mentions);

        final List<CommentLike> commentLikes = commentLikeRepository.findAllByComment(comment);
        commentLikeRepository.deleteAllInBatch(commentLikes);

        commentRepository.delete(comment);

        return new StatusResponse(true);
    }

    private void registerHashtags(Post post, String content) {
        final Set<String> names = new HashSet<>(stringExtractUtil.extractHashtags(content));
        final Map<String, Hashtag> hashtagMap = hashtagRepository.findAllByNameIn(names).stream()
                .collect(Collectors.toMap(Hashtag::getName, h -> h));
        final List<HashtagPost> newHashtagPost = new ArrayList<>();
        names.forEach(name -> {
            Hashtag hashtag;
            if (hashtagMap.containsKey(name)) {
                hashtag = hashtagMap.get(name);
                hashtag.upCount();
            }
            else
                hashtag = hashtagRepository.save(new Hashtag(name));
            newHashtagPost.add(new HashtagPost(hashtag, post));
        });
        hashtagPostRepository.saveAllBatch(newHashtagPost);
    }

    private void unregisterHashtags(Post post) {
        final Set<String> names = new HashSet<>(stringExtractUtil.extractHashtags(post.getContent()));
        final Map<String, Hashtag> hashtagMap = hashtagRepository.findAllByNameIn(names).stream()
                .collect(Collectors.toMap(Hashtag::getName, h -> h));
        final List<Hashtag> deleteHashtags = new ArrayList<>();
        names.forEach(name -> {
            if (hashtagMap.get(name).getCount() == 1)
                deleteHashtags.add(hashtagMap.get(name));
            else
                hashtagMap.get(name).downCount();
        });
        final List<HashtagPost> hashtagPosts = hashtagPostRepository.findAllByHashtagIn(deleteHashtags);
        hashtagPostRepository.deleteAllInBatch(hashtagPosts);
        hashtagRepository.deleteAllInBatch(deleteHashtags);
    }

    public Page<CommentDTO> getCommentDtoPage(Long postId, int page) {
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, 10);
        return commentRepository.findCommentDtoPage(memberId, postId, pageable);
    }

    public Page<CommentDTO> getReplyDtoPage(Long commentId, int page) {
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, 10);
        return commentRepository.findReplyDtoPage(memberId, commentId, pageable);
    }

    public Page<LikeMembersDTO> getPostLikeMembersDtoPage(Long postId, int page, int size) {
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        return postLikeRepository.findLikeMembersDtoPageByPostIdAndMemberId(pageable, postId, memberId);
    }

    public Page<LikeMembersDTO> getCommentLikeMembersDtoPage(Long commentId, int page, int size) {
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        return postLikeRepository.findLikeMembersDtoPageByCommentIdAndMemberId(pageable, commentId, memberId);
    }

    @Transactional
    public StatusResponse sharePost(Long postId, List<String> usernames) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member inviter = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final List<Member> members = memberRepository.findAllByUsernameIn(usernames);

        final List<Member> all = new ArrayList<>();
        all.add(inviter);
        all.addAll(members);

        final List<RoomMember> roomMembers = roomMemberRepository.findAllByMemberIn(all);
        final Map<Member, List<RoomMember>> roomMemberMapGroupByMember = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getMember));
        final Map<Room, List<RoomMember>> roomMemberMapGroupByRoom = roomMembers.stream()
                .collect(Collectors.groupingBy(RoomMember::getRoom));

        members.forEach(member -> {
            Room room = null;

            if (roomMemberMapGroupByMember.containsKey(member)) {
                for (RoomMember taggedRoomMember : roomMemberMapGroupByMember.get(member)) {
                    if (!roomMemberMapGroupByMember.containsKey(inviter))
                        continue;

                    for (RoomMember roomMember : roomMemberMapGroupByMember.get(inviter)) {
                        if (taggedRoomMember.getRoom().getId().equals(roomMember.getRoom().getId())
                                && roomMemberMapGroupByRoom.get(roomMember.getRoom()).size() == 2) {
                            room = roomMember.getRoom();
                            break;
                        }
                    }
                    if (room != null)
                        break;
                }
            }

            if (room == null) {
                room = roomRepository.save(new Room(inviter));
                roomMemberRepository.save(new RoomMember(inviter, room));
                roomMemberRepository.save(new RoomMember(member, room));
            }

            final MessagePost message = messagePostRepository.save(new MessagePost(post, inviter, room));
            roomUnreadMemberRepository.save(new RoomUnreadMember(room, message, member));

            final List<Member> privateRoomMembers = List.of(inviter, member);
            for (Member m : privateRoomMembers) {
                final Optional<JoinRoom> joinRoom = joinRoomRepository.findByMemberAndRoom(m, room);
                if (joinRoom.isPresent())
                    joinRoom.get().updateMessage(message);
                else
                    joinRoomRepository.save(new JoinRoom(room, m, message));
            }

            final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDTO(message));
            messagingTemplate.convertAndSend("/sub/" + member.getUsername(), response);
        });

        return new StatusResponse(true);
    }
}
