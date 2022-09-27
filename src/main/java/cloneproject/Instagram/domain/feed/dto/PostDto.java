package cloneproject.Instagram.domain.feed.dto;

import static cloneproject.Instagram.global.util.ConstantUtils.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostDto {

	private Long postId;
	private String postContent;
	private List<String> mentionsOfContent = new ArrayList<>();
	private List<String> hashtagsOfContent = new ArrayList<>();
	private List<PostImageDto> postImages = new ArrayList<>();
	private LocalDateTime postUploadDate;
	private MemberDto member;
	private long postCommentsCount;
	private long postLikesCount;
	private boolean postBookmarkFlag;
	private boolean postLikeFlag;
	private boolean commentOptionFlag;
	private boolean likeOptionFlag;
	private boolean isFollowing;
	private String followingMemberUsernameLikedPost = EMPTY;
	private List<CommentDto> recentComments = new ArrayList<>();

	@QueryProjection
	public PostDto(Long postId, String postContent, LocalDateTime postUploadDate, Member member, int postCommentsCount,
		int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentOptionFlag,
		boolean likeOptionFlag, boolean isFollowing) {
		this.postId = postId;
		this.postContent = postContent;
		this.postUploadDate = postUploadDate;
		this.member = new MemberDto(member);
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
		this.postBookmarkFlag = postBookmarkFlag;
		this.postLikeFlag = postLikeFlag;
		this.commentOptionFlag = commentOptionFlag;
		this.likeOptionFlag = likeOptionFlag;
		this.isFollowing = isFollowing;
	}

	@QueryProjection
	public PostDto(Long postId, String postContent, LocalDateTime postUploadDate, Member member, int postCommentsCount,
		int postLikesCount, boolean commentOptionFlag, boolean likeOptionFlag) {
		this.postId = postId;
		this.postContent = postContent;
		this.postUploadDate = postUploadDate;
		this.member = new MemberDto(member);
		this.postCommentsCount = postCommentsCount;
		this.postLikesCount = postLikesCount;
		this.postBookmarkFlag = false;
		this.postLikeFlag = false;
		this.commentOptionFlag = commentOptionFlag;
		this.likeOptionFlag = likeOptionFlag;
		this.isFollowing = false;
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

	public void setPostLikesCount(long postLikesCount) {
		this.postLikesCount = postLikesCount;
	}

	public void setHashtagsOfContent(List<String> hashtagsOfContent) {
		this.hashtagsOfContent = hashtagsOfContent;
	}

	public void setMentionsOfContent(List<String> mentionsOfContent) {
		this.mentionsOfContent = mentionsOfContent;
	}

}
