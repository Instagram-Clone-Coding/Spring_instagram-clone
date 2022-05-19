package cloneproject.Instagram.domain.feed.service;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostTag;
import cloneproject.Instagram.domain.feed.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostTagService {

    private final PostTagRepository postTagRepository;

    @Transactional
    public void deleteAll(List<PostImage> postImages) {
        final List<PostTag> postTags = postTagRepository.findAllByPostImageIn(postImages);
        postTagRepository.deleteAllInBatch(postTags);
    }

    @Transactional
    public void saveAll(List<PostImageTagRequest> tags) {
        postTagRepository.savePostTags(tags);
    }

}
