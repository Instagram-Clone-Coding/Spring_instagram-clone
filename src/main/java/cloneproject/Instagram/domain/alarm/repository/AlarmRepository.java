package cloneproject.Instagram.domain.alarm.repository;

import cloneproject.Instagram.domain.alarm.dto.AlarmType;
import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.alarm.repository.jdbc.AlarmRepositoryJdbc;
import cloneproject.Instagram.domain.alarm.repository.querydsl.AlarmRepositoryQuerydsl;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.member.entity.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryQuerydsl, AlarmRepositoryJdbc {

	void deleteByTypeAndAgentAndTargetAndPost(AlarmType type, Member agent, Member target, Post post);

	void deleteByTypeAndAgentAndTargetAndComment(AlarmType type, Member agent, Member target, Comment comment);

	void deleteByTypeAndAgentAndTargetAndFollow(AlarmType type, Member agent, Member target, Follow follow);

	List<Alarm> findAllByPost(Post post);

	List<Alarm> findAllByCommentIn(List<Comment> comments);

}
