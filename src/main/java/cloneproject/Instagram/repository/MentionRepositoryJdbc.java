package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.member.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface MentionRepositoryJdbc {

    void savePostMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, LocalDateTime now);

    void saveCommentMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, Long commentId, LocalDateTime now);
}
