package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.dto.error.ErrorResponse.FieldError;
import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.dto.post.PostResponse;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Bookmark;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.entity.post.PostImage;
import cloneproject.Instagram.entity.post.PostLike;
import cloneproject.Instagram.exception.*;
import cloneproject.Instagram.repository.BookmarkRepository;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.PostImageRepository;
import cloneproject.Instagram.repository.PostLikeRepository;
import cloneproject.Instagram.repository.post.PostRepository;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    public Long upload(String content, List<MultipartFile> postImages, List<PostImageTagRequest> postImageTags) {
        if (postImages.isEmpty())
            throw new PostImageInvalidException();
        validatePostImageTags(postImages, postImageTags);
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId)).orElseThrow(MemberDoesNotExistException::new);

        Post post = Post.builder()
                .content(content)
                .member(member)
                .build();
        postRepository.save(post);

        final List<Image> images = postImages.stream()
                .map(pi -> {
                    try {
                        return uploader.uploadImage(pi, "post");
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                })
                .collect(Collectors.toList());
        postRepository.savePostImages(images, post.getId());

        final List<Long> postImageIds = postImageRepository.findAllByPostId(post.getId()).stream()
                .map(PostImage::getId)
                .collect(Collectors.toList());

        int idx = postImageTags.get(0).getId().intValue();
        for (PostImageTagRequest postImageTag : postImageTags) {
            if (idx != postImageTag.getId())
                idx = postImageTag.getId().intValue();
            postImageTag.setId(postImageIds.get(idx - 1));
        }

        List<String> taggedMemberUsernames = postImageTags.stream().map(PostImageTagRequest::getUsername).collect(Collectors.toList());
        List<Member> taggedMembers = memberRepository.findAllByUsernames(taggedMemberUsernames);
        for(Member taggedMember: taggedMembers){
            alarmService.alert(AlarmType.MEMBER_TAGGED_ALARM, taggedMember, post.getId());
        }
        postRepository.savePostTags(postImageTags);

        return post.getId();
    }

    private void validatePostImageTags(List<MultipartFile> postImages, List<PostImageTagRequest> postImageTags) {
        if (!postImageTags.isEmpty()) {
            postImageTags.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
            List<FieldError> errors = new ArrayList<>();
            for (PostImageTagRequest postImageTag : postImageTags) {
                final String username = postImageTag.getUsername();
                final long tagX = postImageTag.getTagX() == null ? -1L : postImageTag.getTagX();
                final long tagY = postImageTag.getTagY() == null ? -1L : postImageTag.getTagY();
                final long postImageId = postImageTag.getId() == null ? -1L : postImageTag.getId();

                if (username.isBlank() || memberRepository.findByUsername(username).isEmpty())
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
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        if (postLikeRepository.findByMemberIdAndPostId(memberId, postId).isPresent())
            throw new PostLikeAlreadyExistException();
        postLikeRepository.save(new PostLike(member, post));
        alarmService.alert(AlarmType.POST_LIKES_ALARM, post.getMember(), post.getId());
        return true;
    }

    @Transactional
    public boolean unlikePost(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final PostLike postLike = postLikeRepository.findByMemberIdAndPostId(memberId, postId).orElseThrow(PostLikeNotFoundException::new);
        postLikeRepository.delete(postLike);
        return true;
    }

    @Transactional
    public boolean savePost(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        if (bookmarkRepository.findByMemberIdAndPostId(memberId, postId).isPresent())
            throw new BookmarkAlreadyExistException();
        bookmarkRepository.save(new Bookmark(member, post));
        return true;
    }

    @Transactional
    public boolean unsavePost(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);
        final Bookmark bookmark = bookmarkRepository.findByMemberIdAndPostId(memberId, postId).orElseThrow(BookmarkNotFoundException::new);
        bookmarkRepository.delete(bookmark);
        return true;
    }
}
