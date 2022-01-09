package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.post.PostDTO;
import cloneproject.Instagram.dto.post.PostImageTagDTO;
import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.entity.post.PostImage;
import cloneproject.Instagram.entity.post.PostTag;
import cloneproject.Instagram.exception.*;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.PostImageRepository;
import cloneproject.Instagram.repository.PostRepository;
import cloneproject.Instagram.repository.PostTagRepository;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.ImageType;
import cloneproject.Instagram.vo.Tag;
import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader uploader;

    @Transactional
    public Long create(String content) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        final Member member = memberRepository.findById(Long.valueOf(memberId)).orElseThrow(MemberDoesNotExistException::new);
        Post post = Post.builder()
                .member(member)
                .content(content)
                .build();
        return postRepository.save(post).getId();
    }

    @Transactional
    public List<Long> uploadImages(Long id, MultipartFile[] uploadImages) {
        final Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        List<Long> imageIds = new ArrayList<>();

        Arrays.stream(uploadImages)
                .forEach(ui -> {
                    try {
                        Image image = uploader.uploadImage(ui, "post");
                        final PostImage postImage = postImageRepository.save(
                                PostImage.builder()
                                        .post(post)
                                        .image(image)
                                        .build());
                        final Long imageId = postImageRepository.save(postImage).getId();
                        imageIds.add(imageId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        return imageIds;
    }

    @Transactional
    public void addTags(List<PostImageTagRequest> requests) {
        if (requests.isEmpty())
            throw new NoPostImageTagException();

        requests
                .forEach(r -> {
                    final PostImage postImage = postImageRepository.findById(r.getId()).orElseThrow(PostImageNotFoundException::new);
                    final List<PostImageTagDTO> postImageTagDTOs = r.getPostImageTagDTOs();
                    for (PostImageTagDTO postImageTagDTO : postImageTagDTOs) {
                        if (postImageTagDTO.getTagX() < 0 || postImageTagDTO.getTagX() > 100 || postImageTagDTO.getTagY() < 0 || postImageTagDTO.getTagY() > 100)
                            throw new InvalidTagLocationException();
                        final String username = postImageTagDTO.getUsername();
                        memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);
                        Tag tag = Tag.builder()
                                .x(postImageTagDTO.getTagX())
                                .y(postImageTagDTO.getTagY())
                                .username(username)
                                .build();

                        PostTag postTag = PostTag.builder()
                                .postImage(postImage)
                                .tag(tag)
                                .build();

                        postTagRepository.save(postTag);
                    }
                });
    }

    public Page<PostDTO> getPostDtoPage(int size, int page) {
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size, Sort.by(DESC, "id"));
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
}
