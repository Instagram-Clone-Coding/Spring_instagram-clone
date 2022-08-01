package cloneproject.Instagram.domain.dm.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSimpleInfo {

	private String username;
	private String name;
	private String imageUrl;

	public MemberSimpleInfo(Member member) {
		this.username = member.getUsername();
		this.name = member.getName();
		this.imageUrl = member.getImage().getImageUrl();
	}

}
