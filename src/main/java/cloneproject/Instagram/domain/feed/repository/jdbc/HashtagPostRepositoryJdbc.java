package cloneproject.Instagram.domain.feed.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.feed.entity.HashtagPost;

public interface HashtagPostRepositoryJdbc {

    void saveAllBatch(List<HashtagPost> newHashtagPost);
}
