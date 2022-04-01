package cloneproject.Instagram.domain.feed.repository.jdbc;

import cloneproject.Instagram.global.vo.Image;

import java.util.List;

public interface PostImageRepositoryJdbc {

	void savePostImages(List<Image> images, Long postId, List<String> altTexts);
}
