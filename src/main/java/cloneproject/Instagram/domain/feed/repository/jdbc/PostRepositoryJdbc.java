package cloneproject.Instagram.domain.feed.repository.jdbc;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;
import cloneproject.Instagram.global.vo.Image;

import java.util.List;

public interface PostRepositoryJdbc {

    void savePostImages(List<Image> images, Long postId, List<String> altTexts);
    void savePostTags(List<PostImageTagRequest> postImageTagMap);
}
