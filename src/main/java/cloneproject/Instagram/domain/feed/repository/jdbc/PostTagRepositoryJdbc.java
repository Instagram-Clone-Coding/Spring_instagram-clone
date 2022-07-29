package cloneproject.Instagram.domain.feed.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.domain.feed.dto.PostImageTagRequest;

public interface PostTagRepositoryJdbc {

	void savePostTags(List<PostImageTagRequest> postImageTags);

}
