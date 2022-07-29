package cloneproject.Instagram.domain.feed.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPostDto {

	private Long postId;
	private MemberDto member;
	private PostImageDto postImages;
	private boolean hasManyPosts;
	private boolean likeOptionFlag;
	private boolean postLikeFlag;
	private int postCommentsCount;
	private int postLikesCount;

	@Builder
	@QueryProjection
	public MemberPostDto(Long postId, Member member, boolean hasManyPosts, boolean likeOptionFlag, boolean postLikeFlag,
		int postCommentsCount, int postLikesCount) {
		this.postId = postId;
		this.member = new MemberDto(member);
		this.hasManyPosts = hasManyPosts;
		this.likeOptionFlag = likeOptionFlag;
		this.postLikeFlag = postLikeFlag;
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
	}

	@Builder
	@QueryProjection
	public MemberPostDto(Long postId, Member member, boolean hasManyPosts, int postCommentsCount, int postLikesCount) {
		this.postId = postId;
		this.member = new MemberDto(member);
		this.hasManyPosts = hasManyPosts;
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
	}

	public void setPostImages(PostImageDto postImageDto) {
		this.postImages = postImageDto;
	}

	public void setPostLikesCount(int postLikesCount) {
		this.postLikesCount = postLikesCount;
	}

}
