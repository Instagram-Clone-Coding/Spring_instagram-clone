package cloneproject.Instagram.util.domain.story;

import cloneproject.Instagram.domain.member.entity.Member;
import cloneproject.Instagram.domain.story.entity.Story;
import cloneproject.Instagram.global.vo.Image;
import cloneproject.Instagram.util.ImageUtils;
import cloneproject.Instagram.util.domain.member.MemberUtils;

public class StoryUtils {

	public static Story newInstance() {
		final Member member = MemberUtils.newInstance();
		final Image image = ImageUtils.newInstance();
		return of(member, image);
	}

	public static Story of(Member member, Image image) {
		return Story.builder()
			.member(member)
			.image(image)
			.build();
	}

}
