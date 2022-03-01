package cloneproject.Instagram.repository.post;

import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.vo.Image;

import java.util.List;

public interface PostRepositoryJdbc {

    void savePostImages(List<Image> images, Long postId, List<String> altTexts);
    void savePostTags(List<PostImageTagRequest> postImageTagMap);
}
