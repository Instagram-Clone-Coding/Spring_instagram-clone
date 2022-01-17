package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.post.PostImageTagRequest;
import cloneproject.Instagram.vo.Image;

import java.util.List;

public interface PostRepositoryJdbc {

    void savePostImages(List<Image> images, Long postId);
    void savePostTags(List<PostImageTagRequest> postImageTagMap);
}
