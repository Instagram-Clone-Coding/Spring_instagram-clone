package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.post.HashtagPost;

import java.util.List;

public interface HashtagPostRepositoryJdbc {

    void saveAllBatch(List<HashtagPost> newHashtagPost);
}
