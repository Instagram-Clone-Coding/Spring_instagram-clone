package cloneproject.Instagram.domain.feed.dto;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

import com.querydsl.core.annotations.QueryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

	private Long postId;
	private String postContent;
	private LocalDateTime postUploadDate;
	private MemberDto member;
	private int postLikesCount;
	private boolean postBookmarkFlag;
	private boolean postLikeFlag;
	private boolean commentFlag;
	private boolean likeFlag;
	private String followingMemberUsernameLikedPost = "";
	private List<PostImageDto> postImageDtos = new ArrayList<>();
	private List<CommentDto> commentDtos = new ArrayList<>();

	@QueryProjection
	public PostResponse(Long postId, String postContent, LocalDateTime postUploadDate, Member member,
		int postLikesCount, boolean postBookmarkFlag, boolean postLikeFlag, boolean commentFlag, boolean likeFlag) {
		this.postId = postId;
		this.postContent = postContent;
		this.postUploadDate = postUploadDate;
		this.member = new MemberDto(member);
		this.postLikesCount = postLikesCount;
		this.postBookmarkFlag = postBookmarkFlag;
		this.postLikeFlag = postLikeFlag;
		this.commentFlag = commentFlag;
		this.likeFlag = likeFlag;
	}

}
