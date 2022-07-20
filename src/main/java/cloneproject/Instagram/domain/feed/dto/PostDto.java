package cloneproject.Instagram.domain.feed.dto;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {

	private Long postId;
	private String postContent;
	private List<PostImageDto> postImages = new ArrayList<>();
	private LocalDateTime postUploadDate;
	private MemberDto member;
	private int postCommentsCount;
	private int postLikesCount;
	private boolean postBookmarkFlag;
	private boolean postLikeFlag;
	private boolean commentFlag;
	private boolean likeFlag;
	private String followingMemberUsernameLikedPost = "";
	private List<CommentDto> recentComments = new ArrayList<>();

	@QueryProjection
	public PostDto(Long postId, String postContent, LocalDateTime postUploadDate, Member member, int postCommentsCount,
		int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentFlag, boolean likeFlag) {
		this.postId = postId;
		this.postContent = postContent;
		this.postUploadDate = postUploadDate;
		this.member = new MemberDto(member);
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
		this.postBookmarkFlag = postBookmarkFlag;
		this.postLikeFlag = postLikeFlag;
		this.commentFlag = commentFlag;
		this.likeFlag = likeFlag;
	}

	public void setPostImages(List<PostImageDto> postImageDtos) {
		this.postImages = postImageDtos;
	}

	public void setFollowingMemberUsernameLikedPost(String followingMemberUsernameLikedPost) {
		this.followingMemberUsernameLikedPost = followingMemberUsernameLikedPost;
	}

	public void setRecentComments(List<CommentDto> commentDtos) {
		this.recentComments = commentDtos;
	}

}
