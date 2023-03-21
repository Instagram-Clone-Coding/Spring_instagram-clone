package cloneproject.Instagram.domain.feed.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Data;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.member.dto.MemberDto;
import cloneproject.Instagram.domain.member.entity.Member;

@Data
@NoArgsConstructor
public class CommentDto {

	@JsonIgnore
	private Long postId;

	private Long id;
	private MemberDto member;
	private String content;
	private LocalDateTime uploadDate;
	private int commentLikesCount;
	private boolean commentLikeFlag;
	private int repliesCount;
	private List<String> mentionsOfContent = new ArrayList<>();
	private List<String> hashtagsOfContent = new ArrayList<>();

	public CommentDto(Long id, Member member, String content, LocalDateTime uploadDate, int commentLikesCount, boolean commentLikeFlag, int repliesCount, List<String> mentionsOfContent,
		List<String> hashtagsOfContent) {
		this.id = id;
		this.member = new MemberDto(member);
		this.content = content;
		this.uploadDate = uploadDate;
		this.commentLikesCount = commentLikesCount;
		this.commentLikeFlag = commentLikeFlag;
		this.repliesCount = repliesCount;
		this.mentionsOfContent = mentionsOfContent;
		this.hashtagsOfContent = hashtagsOfContent;
	}

	@QueryProjection
	public CommentDto(Long postId, Long id, Member member, String content, LocalDateTime uploadDate,
		int commentLikesCount, boolean commentLikeFlag, int repliesCount) {
		this.postId = postId;
		this.id = id;
		this.member = new MemberDto(member);
		this.content = content;
		this.uploadDate = uploadDate;
		this.commentLikesCount = commentLikesCount;
		this.commentLikeFlag = commentLikeFlag;
		this.repliesCount = repliesCount;
	}

	@QueryProjection
	public CommentDto(Long postId, Long id, Member member, String content, LocalDateTime uploadDate,
		int commentLikesCount, int repliesCount) {
		this.postId = postId;
		this.id = id;
		this.member = new MemberDto(member);
		this.content = content;
		this.uploadDate = uploadDate;
		this.commentLikesCount = commentLikesCount;
		this.commentLikeFlag = false;
		this.repliesCount = repliesCount;
	}

	public void setMentionsOfContent(List<String> mentionsOfContent) {
		this.mentionsOfContent = mentionsOfContent;
	}

	public void setHashtagsOfContent(List<String> hashtagsOfContent) {
		this.hashtagsOfContent = hashtagsOfContent;
	}

}
