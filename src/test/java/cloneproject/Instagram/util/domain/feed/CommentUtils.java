package cloneproject.Instagram.util.domain.feed;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

public class CommentUtils {

	public static Comment newInstance(Post post, Comment parent, Member member) {
		final String content = RandomStringUtils.random(20, true, true);
		return of(post, parent, member, content);
	}

	public static Comment newInstance(Post post, Member member) {
		final String content = RandomStringUtils.random(20, true, true);
		return of(post, null, member, content);
	}

	public static Comment of(Post post, Comment parent, Member member, String content) {
		return Comment.builder()
			.post(post)
			.parent(parent)
			.member(member)
			.content(content)
			.build();
	}

}
