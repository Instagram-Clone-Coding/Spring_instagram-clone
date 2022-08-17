package cloneproject.Instagram.util.domain.feed;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostImage;
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

	public static List<Comment> newInstances(Post post, Member member, long commentCount) {
		final List<Comment> comments = new ArrayList<>();
		for (long count = 1; count <= commentCount; count++) {
			final Comment comment = newInstance(post, member);
			comments.add(comment);
		}
		return comments;
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
