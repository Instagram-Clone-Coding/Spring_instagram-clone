package cloneproject.Instagram.util.domain.feed;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.util.ImageUtils;

public class PostImageUtils {

	public static PostImage newInstance(Post post) {
		final String altText = RandomStringUtils.random(20, true, true);
		return of(post, ImageUtils.newInstance(), altText);
	}

	public static PostImage of(Post post, Image image, String altText) {
		return PostImage.builder()
			.post(post)
			.image(image)
			.altText(altText)
			.build();
	}

}
