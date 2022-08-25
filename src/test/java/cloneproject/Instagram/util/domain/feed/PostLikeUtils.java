package cloneproject.Instagram.util.domain.feed;

import java.util.ArrayList;
import java.util.List;

import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.feed.entity.PostLike;
import cloneproject.Instagram.domain.member.entity.Member;

public class PostLikeUtils {

	public static List<PostLike> newInstancesForEachMember(Post post, List<Member> members) {
		final List<PostLike> postLikes = new ArrayList<>();
		for (Member member : members) {
			final PostLike postLike = of(post, member);
			postLikes.add(postLike);
		}
		return postLikes;
	}

	public static PostLike of(Post post, Member member) {
		return PostLike.builder()
			.post(post)
			.member(member)
			.build();
	}

}
