package cloneproject.Instagram.util.domain.member;

import java.util.ArrayList;
import java.util.List;

import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.member.entity.Member;

public class FollowUtils {

	public static List<Follow> newFollowerInstances(List<Member> members, Member followMember) {
		final List<Follow> follows = new ArrayList<>();
		for (Member member : members) {
			final Follow follow = of(member, followMember);
			follows.add(follow);
		}
		return follows;
	}

	public static List<Follow> newFollowingInstances(Member member, List<Member> followMembers) {
		final List<Follow> follows = new ArrayList<>();
		for (Member followMember : followMembers) {
			final Follow follow = of(member, followMember);
			follows.add(follow);
		}
		return follows;
	}

	public static Follow of(Member member, Member followMember) {
		return Follow.builder()
			.member(member)
			.followMember(followMember)
			.build();
	}

}
