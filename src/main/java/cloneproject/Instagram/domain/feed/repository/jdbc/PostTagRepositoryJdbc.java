package cloneproject.Instagram.domain.feed.repository.jdbc;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;

import java.util.List;

public interface PostTagRepositoryJdbc {

	void savePostTags(List<PostImageTagRequest> postImageTags);
}
