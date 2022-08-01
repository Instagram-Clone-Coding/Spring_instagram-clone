package cloneproject.Instagram.domain.alarm.repository.jdbc;

import java.time.LocalDateTime;
import java.util.List;

import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.member.entity.Member;

public interface AlarmRepositoryJdbc {

	void saveMentionPostAlarms(Member agent, List<Member> targets, Post post, LocalDateTime now);

	void saveMentionCommentAlarms(Member agent, List<Member> targets, Post post, Comment comment, LocalDateTime now);

}
