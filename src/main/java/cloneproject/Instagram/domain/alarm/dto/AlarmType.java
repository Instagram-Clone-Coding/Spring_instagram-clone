package cloneproject.Instagram.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {

	FOLLOW("{agent.username}님이 회원님을 팔로우하기 시작했습니다."),

	LIKE_POST("{agent.username}님이 회원님의 사진을 좋아합니다."),
	MENTION_POST("{agent.username}님이 게시물에서 회원님을 언급했습니다: {post.content}"),

	COMMENT("{agent.username}님이 댓글을 남겼습니다: {comment.content}"),
	LIKE_COMMENT("{agent.username}님이 회원님의 댓글을 좋아합니다: {comment.content}"),
	MENTION_COMMENT("{agent.username}님이 댓글에서 회원님을 언급했습니다: {comment.content}"),
	;

	private String message;

}
