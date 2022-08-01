package cloneproject.Instagram.domain.mention.repository.jdbc;

import java.time.LocalDateTime;
import java.util.List;

import cloneproject.Instagram.domain.member.entity.Member;

public interface MentionRepositoryJdbc {

	void savePostMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, LocalDateTime now);

	void saveCommentMentionsBatch(Long memberId, List<Member> mentionedMembers, Long postId, Long commentId,
		LocalDateTime now);

}
