package cloneproject.Instagram.domain.feed.repository.jdbc;

import java.util.List;

import cloneproject.Instagram.global.vo.Image;

public interface PostImageRepositoryJdbc {

	void savePostImages(List<Image> images, Long postId, List<String> altTexts);

}
