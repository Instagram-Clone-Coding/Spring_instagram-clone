package cloneproject.Instagram.util.domain.feed;

import java.util.ArrayList;
import java.util.List;

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

	public static List<PostImage> newInstances(Post post, long postImageCount) {
		final List<PostImage> postImages = new ArrayList<>();
		for (long count = 1; count <= postImageCount; count++) {
			final PostImage postImage = newInstance(post);
			postImages.add(postImage);
		}
		return postImages;
	}

	public static List<PostImage> newInstancesForEachPost(List<Post> posts, long postImageCount) {
		final List<PostImage> postImages = new ArrayList<>();
		for(Post post: posts) {
			final List<PostImage> postImageList = newInstances(post, postImageCount);
			postImages.addAll(postImageList);
		}
		return postImages;
	}

	public static PostImage of(Post post, Image image, String altText) {
		return PostImage.builder()
			.post(post)
			.image(image)
			.altText(altText)
			.build();
	}

}
