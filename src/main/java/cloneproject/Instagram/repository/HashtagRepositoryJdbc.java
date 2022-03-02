package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import cloneproject.Instagram.entity.post.Post;

import java.util.List;

public interface HashtagRepositoryJdbc {

    void saveAllBatch(List<Hashtag> hashtags, Post post);
}
