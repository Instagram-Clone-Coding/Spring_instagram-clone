package cloneproject.Instagram.util.domain.feed;

import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.domain.feed.entity.PostTag;
import cloneproject.Instagram.domain.feed.vo.Tag;

public class PostTagUtils {

	public static PostTag newInstance(PostImage postImage, String username) {
		final Double x = Math.random() * 100;
		final Double y = Math.random() * 100;
		final Tag tag = Tag.builder()
			.x(x)
			.y(y)
			.username(username)
			.build();
		return of(postImage, tag);
	}

	public static PostTag of(PostImage postImage, Tag tag) {
		return PostTag.builder()
			.postImage(postImage)
			.tag(tag)
			.build();
	}

}
