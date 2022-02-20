package cloneproject.Instagram.repository;

import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface AlarmRepositoryJdbc {

    void saveMentionPostAlarms(Member agent, List<Member> targets, Post post, LocalDateTime now);

    void saveMentionCommentAlarms(Member agent, List<Member> targets, Post post, Comment comment, LocalDateTime now);
}
