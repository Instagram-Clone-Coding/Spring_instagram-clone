package cloneproject.Instagram.domain.hashtag.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.hashtag.entity.HashtagPost;

public interface HashtagPostRepositoryJdbc {

	void saveAllBatch(List<HashtagPost> newHashtagPost);

}
