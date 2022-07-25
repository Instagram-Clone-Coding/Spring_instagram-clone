package cloneproject.Instagram.domain.member.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import cloneproject.Instagram.domain.follow.dto.FollowDto;
import cloneproject.Instagram.global.vo.Image;

@ApiModel("유저 프로필 조회 응답 모델")
@Getter
public class UserProfileResponse {

	@ApiModelProperty(value = "유저네임", example = "dlwlrma")
	private String memberUsername;

	@ApiModelProperty(value = "이름", example = "이지금")
	private String memberName;

	@ApiModelProperty(value = "웹사이트", example = "http://localhost:8080")
	private String memberWebsite;

	@ApiModelProperty(value = "프로필사진")
	private Image memberImage;

	@ApiModelProperty(value = "팔로잉 여부", example = "true")
	private boolean isFollowing;

	@ApiModelProperty(value = "팔로워 여부", example = "false")
	private boolean isFollower;

	@ApiModelProperty(value = "차단 여부", example = "false")
	private boolean isBlocking;

	@ApiModelProperty(value = "차단당한 여부", example = "false")
	private boolean isBlocked;

	@ApiModelProperty(value = "소개", example = "안녕하세요")
	private String memberIntroduce;

	@ApiModelProperty(value = "포스팅 수", example = "90")
	private Long memberPostsCount;

	@ApiModelProperty(value = "팔로잉 수", example = "100")
	private Long memberFollowingsCount;

	@ApiModelProperty(value = "팔로워 수", example = "100")
	private Long memberFollowersCount;

	@ApiModelProperty(value = "내 팔로잉 중 해당 유저를 팔로우 하는 사람", example = "dlwlrma")
	private List<FollowDto> followingMemberFollow;
	private int followingMemberFollowCount;

	@ApiModelProperty(value = "본인 여부", example = "false")
	private boolean isMe;

	private boolean hasStory;

	@QueryProjection
	public UserProfileResponse(String username, String name, String website, Image image,
		boolean isFollowing, boolean isFollower, boolean isBlocking, boolean isBlocked,
		String introduce, Long postsCount, Long followingsCount, Long followersCount,
		boolean isMe) {
		this.memberUsername = username;
		this.memberName = name;
		this.memberWebsite = website;
		this.memberImage = image;
		this.isFollowing = isFollowing;
		this.isFollower = isFollower;
		this.isBlocking = isBlocking;
		this.isBlocked = isBlocked;
		this.memberIntroduce = introduce;
		this.memberPostsCount = postsCount;
		this.memberFollowingsCount = followingsCount;
		this.memberFollowersCount = followersCount;
		this.isMe = isMe;
		this.hasStory = false;
		checkBlock();
	}

	public void setFollowingMemberFollow(List<FollowDto> followingMemberFollow, int maxCount) {
		this.followingMemberFollow = followingMemberFollow
			.stream()
			.limit(maxCount)
			.collect(Collectors.toList());
		this.followingMemberFollowCount = followingMemberFollow.size() - this.followingMemberFollow.size();
	}

	public void setHasStory(boolean hasStory) {
		this.hasStory = hasStory;
		checkBlock();
	}

	private void checkBlock() {
		if (this.isBlocked || this.isBlocking) {
			this.memberPostsCount = 0L;
			this.memberFollowingsCount = 0L;
			this.memberFollowersCount = 0L;
			this.hasStory = false;
		}
	}

}
