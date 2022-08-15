package cloneproject.Instagram.util.domain.feed;

import org.apache.commons.lang3.RandomStringUtils;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

public class PostUtils {

	public static Post newInstance(Member member) {
		final String content = RandomStringUtils.random(20, true, true);
		return of(member, content, true, true);
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
