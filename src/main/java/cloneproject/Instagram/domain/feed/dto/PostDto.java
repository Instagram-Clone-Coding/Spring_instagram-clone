package cloneproject.Instagram.domain.feed.dto;

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
	private List<String> existentMentionsOfContent = new ArrayList<>();
	private List<String> nonExistentMentionsOfContent = new ArrayList<>();
	private List<String> hashtagsOfContent = new ArrayList<>();
	private List<PostImageDto> postImages = new ArrayList<>();
	private LocalDateTime postUploadDate;
	private MemberDto member;
	private int postCommentsCount;
	private int postLikesCount;
	private boolean postBookmarkFlag;
	private boolean postLikeFlag;
	private boolean commentOptionFlag;
	private boolean likeOptionFlag;
	private String followingMemberUsernameLikedPost = "";
	private List<CommentDto> recentComments = new ArrayList<>();

	@QueryProjection
	public PostDto(Long postId, String postContent, LocalDateTime postUploadDate, Member member, int postCommentsCount,
		int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentOptionFlag,
		boolean likeOptionFlag) {
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

	public void setPostLikesCount(int postLikesCount) {
		this.postLikesCount = postLikesCount;
	}

	public void setHashtagsOfContent(List<String> hashtagsOfContent) {
		this.hashtagsOfContent = hashtagsOfContent;
	}

	public void setExistentMentionsOfContent(List<String> existentMentionsOfContent) {
		this.existentMentionsOfContent = existentMentionsOfContent;
	}

	public void setNonExistentMentionsOfContent(List<String> nonExistentMentionsOfContent) {
		this.nonExistentMentionsOfContent = nonExistentMentionsOfContent;
	}

}
