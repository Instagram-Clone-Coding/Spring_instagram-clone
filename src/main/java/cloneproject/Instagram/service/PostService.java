package cloneproject.Instagram.service;

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
import cloneproject.Instagram.util.JwtUtil;
import cloneproject.Instagram.vo.Image;
import cloneproject.Instagram.vo.ImageType;
import cloneproject.Instagram.vo.Tag;
import com.google.common.base.Enums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Long create(String content, String authorization) {
        final Authentication authentication = jwtUtil.getAuthentication(authorization.substring(7));
        final Member member = memberRepository.findById(Long.valueOf(authentication.getName())).orElseThrow(MemberDoesNotExistException::new);
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

        for (MultipartFile uploadImage : uploadImages) {
            MultipartFile file = uploadImage;
            // TODO: url 수정
            String url = "C:\\spring";
            String originalName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalName).toUpperCase();
            String fileName = FilenameUtils.getBaseName(originalName);

            if(!Enums.getIfPresent(ImageType.class, extension).isPresent())
                throw new NotSupportedImageTypeException();

            Image image = Image.builder()
                    .imageUrl(url)
                    .imageType(ImageType.valueOf(extension))
                    .imageName(fileName)
                    .imageUUID(UUID.randomUUID().toString())
                    .build();

            final PostImage postImage = postImageRepository.save(
                    PostImage.builder()
                            .post(post)
                            .image(image)
                            .build());
            // TODO: 이미지 url에 저장

            final Long imageId = postImageRepository.save(postImage).getId();
            imageIds.add(imageId);
        }

        return imageIds;
    }

    @Transactional
    public void addTags(List<PostImageTagRequest> requests) {
        if(requests.isEmpty())
            throw new NoPostImageTagException();

        for (PostImageTagRequest request : requests) {
            final PostImage postImage = postImageRepository.findById(request.getId()).orElseThrow(PostImageNotFoundException::new);
            final List<PostImageTagDTO> postImageTagDTOs = request.getPostImageTagDTOs();
            for (PostImageTagDTO postImageTagDTO : postImageTagDTOs) {
                // TODO: x, y 범위 검증 코드 추가
                Tag tag = Tag.builder()
                        .x(postImageTagDTO.getTagX())
                        .y(postImageTagDTO.getTagY())
                        .build();

                final String username = postImageTagDTO.getUsername();
                final Member member = memberRepository.findByUsername(username).orElseThrow(MemberDoesNotExistException::new);

                PostTag postTag = PostTag.builder()
                        .postImage(postImage)
                        .tag(tag)
                        .member(member)
                        .build();

                postTagRepository.save(postTag);
            }
        }
    }
}
