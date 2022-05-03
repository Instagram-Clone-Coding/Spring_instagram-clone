package cloneproject.Instagram.domain.alarm.service;

import cloneproject.Instagram.global.util.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.domain.alarm.dto.AlarmDTO;
import cloneproject.Instagram.domain.alarm.dto.AlarmType;
import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.alarm.exception.MismatchedAlarmTypeException;
import cloneproject.Instagram.domain.alarm.repository.AlarmRepository;
import cloneproject.Instagram.domain.feed.entity.Comment;
import cloneproject.Instagram.domain.feed.entity.Post;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final AuthUtil authUtil;

    public Page<AlarmDTO> getAlarms(int page, int size) {
        final Member agent = authUtil.getLoginMember();
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        return alarmRepository.getAlarmDtoPageByMemberId(pageable, agent.getId());
    }

    @Transactional
    public void alert(Member target, Follow follow) {
        final Member agent = authUtil.getLoginMember();
        final Alarm alarm = Alarm.builder()
                .type(FOLLOW)
                .agent(agent)
                .target(target)
                .follow(follow)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void alert(AlarmType type, Member target, Post post) {
        if (!type.equals(LIKE_POST))
            throw new MismatchedAlarmTypeException();

        final Member agent = authUtil.getLoginMember();
        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(post)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void alertBatch(AlarmType type, List<Member> targets, Post post) {
        if (!type.equals(MENTION_POST))
            throw new MismatchedAlarmTypeException();

        final Member agent = authUtil.getLoginMember();
        alarmRepository.saveMentionPostAlarms(agent, targets, post, LocalDateTime.now());
    }

    @Transactional
    public void alertBatch(AlarmType type, List<Member> targets, Post post, Comment comment) {
        if (!type.equals(MENTION_COMMENT))
            throw new MismatchedAlarmTypeException();

        final Member agent = authUtil.getLoginMember();
        alarmRepository.saveMentionCommentAlarms(agent, targets, post, comment, LocalDateTime.now());
    }

    @Transactional
    public void alert(AlarmType type, Member target, Post post, Comment comment) {
        if (!type.equals(COMMENT) && !type.equals(LIKE_COMMENT) && !type.equals(MENTION_COMMENT))
            throw new MismatchedAlarmTypeException();

        final Member agent = authUtil.getLoginMember();
        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(agent)
                .target(target)
                .post(post)
                .comment(comment)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void delete(AlarmType type, Member target, Post post) {
        final Member agent = authUtil.getLoginMember();
        alarmRepository.deleteByTypeAndAgentAndTargetAndPost(type, agent, target, post);
    }
    @Transactional
    public void delete(AlarmType type, Member target, Comment comment) {
        final Member agent = authUtil.getLoginMember();
        alarmRepository.deleteByTypeAndAgentAndTargetAndComment(type, agent, target, comment);
    }

    @Transactional
    public void delete(Member target, Follow follow) {
        final Member agent = authUtil.getLoginMember();
        alarmRepository.deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, agent, target, follow);
    }

    @Transactional
    public void deleteAll(Post post) {
        final List<Alarm> alarms = alarmRepository.findAllByPost(post);
        alarmRepository.deleteAllInBatch(alarms);
    }

    @Transactional
    public void deleteAll(List<Comment> comments) {
        final List<Alarm> alarms = alarmRepository.findAllByCommentIn(comments);
        alarmRepository.deleteAllInBatch(alarms);
    }
}
