package cloneproject.Instagram.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {
    
    POST_LIKES_ALARM("agentUsername이 targetUsername(나)의 postId인 포스트를 좋아합니다", "postId"),
    POST_COMMENT_ALARM("agentUsername이 targetUsername(나)의 postId인 포스트에 댓글을 남겼습니다.", "postId"),
    COMMENT_LIKES_ALARM("agentUsername이 targetUsername(나)의 postId인 포스트에 댓글을 남겼습니다.", "postId"),
    MEMBER_FOLLOW_ALARM("agentUsername이 targetUsername(나)를 팔로우 합니다", "followId"),
    MEMBER_TAGGED_ALARM("agentUsername이 postId인 포스트에서 targetUsername(나)를 태그했습니다", "postId");

    private String alarmMesasge;
    private String firstItemType;

}
