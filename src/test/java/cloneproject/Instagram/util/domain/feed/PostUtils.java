package cloneproject.Instagram.util.domain.feed;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

public class PostUtils {

	public static Post newInstance(Member member) {
		final String content = RandomStringUtils.random(20, true, true);
		return of(member, content, true, true);
	}

	public static List<Post> newInstances(Member member, long postCount) {
		final List<Post> posts = new ArrayList<>();
		for (long count = 1; count <= postCount; count++) {
			final Post post = PostUtils.newInstance(member);
			posts.add(post);
		}
		return posts;
	}

	public static Post of(Member member, String content, boolean commentFlag, boolean likeFlag) {
		return Post.builder()
			.member(member)
			.content(content)
			.commentFlag(commentFlag)
			.likeFlag(likeFlag)
			.build();
	}

}
