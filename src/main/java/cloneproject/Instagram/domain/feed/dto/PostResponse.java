package cloneproject.Instagram.domain.feed.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@AllArgsConstructor
public class PostResponse {

	private Long postId;
	private String postContent;
	private LocalDateTime postUploadDate;
	private MemberDto member;
	private int postLikesCount;
	private boolean postBookmarkFlag;
	private boolean postLikeFlag;
	private boolean commentOptionFlag;
	private boolean likeOptionFlag;
	private String followingMemberUsernameLikedPost = "";
	private List<PostImageDto> postImageDtos = new ArrayList<>();
	private List<CommentDto> commentDtos = new ArrayList<>();

	@QueryProjection
	public PostResponse(Long postId, String postContent, LocalDateTime postUploadDate, Member member,
		int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentOptionFlag, boolean likeOptionFlag) {
		this.postId = postId;
		this.postContent = postContent;
		this.postUploadDate = postUploadDate;
		this.member = new MemberDto(member);
		this.postLikesCount = postLikesCount;
		this.postBookmarkFlag = postBookmarkFlag;
		this.postLikeFlag = postLikeFlag;
		this.commentOptionFlag = commentOptionFlag;
		this.likeOptionFlag = likeOptionFlag;
	}

	public void setCommentDtos(List<CommentDto> commentDtos) {
		this.commentDtos = commentDtos;
	}

	public void setPostImageDtos(List<PostImageDto> postImageDtos) {
		this.postImageDtos = postImageDtos;
	}

	public void setFollowingMemberUsernameLikedPost(String followingMemberUsernameLikedPost) {
		this.followingMemberUsernameLikedPost = followingMemberUsernameLikedPost;
	}

	public void setPostLikesCount(int postLikesCount) {
		this.postLikesCount = postLikesCount;
	}

}
