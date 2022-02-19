package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.alarms.Alarm;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryQuerydsl, AlarmRepositoryJdbc {

    void deleteByTypeAndAgentAndTargetAndPost(AlarmType type, Member agent, Member target, Post post);

    void deleteByTypeAndAgentAndTargetAndComment(AlarmType type, Member agent, Member target, Comment comment);

    void deleteByTypeAndAgentAndTargetAndFollow(AlarmType type, Member agent, Member target, Follow follow);

    List<Alarm> findAllByCommentAndTypeIn(Comment comment, List<AlarmType> alarmTypes);

    List<Alarm> findAllByPost(Post post);
}
