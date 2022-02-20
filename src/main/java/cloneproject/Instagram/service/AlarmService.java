package cloneproject.Instagram.service;

import cloneproject.Instagram.entity.comment.Comment;
import cloneproject.Instagram.entity.member.Follow;
import cloneproject.Instagram.entity.post.Post;
import cloneproject.Instagram.exception.MismatchedAlarmTypeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.dto.alarm.AlarmDTO;
import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.entity.alarms.Alarm;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.AlarmRepository;
import cloneproject.Instagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static cloneproject.Instagram.dto.alarm.AlarmType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    public Page<AlarmDTO> getAlarms(int page, int size) {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, size);
        return alarmRepository.getAlarmDtoPageByMemberId(pageable, Long.valueOf(memberId));
    }

    @Transactional
    public void alert(Member target, Follow follow) {
        final Alarm alarm = Alarm.builder()
                .type(FOLLOW)
                .agent(getAgent())
                .target(target)
                .follow(follow)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void alert(AlarmType type, Member target, Post post) {
        if (!type.equals(LIKE_POST))
            throw new MismatchedAlarmTypeException();

        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(getAgent())
                .target(target)
                .post(post)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void alertBatch(AlarmType type, List<Member> targets, Post post) {
        if (!type.equals(MENTION_POST))
            throw new MismatchedAlarmTypeException();

        alarmRepository.saveMentionPostAlarms(getAgent(), targets, post, LocalDateTime.now());
    }

    @Transactional
    public void alertBatch(AlarmType type, List<Member> targets, Post post, Comment comment) {
        if (!type.equals(MENTION_COMMENT))
            throw new MismatchedAlarmTypeException();

        alarmRepository.saveMentionCommentAlarms(getAgent(), targets, post, comment, LocalDateTime.now());
    }

    @Transactional
    public void alert(AlarmType type, Member target, Post post, Comment comment) {
        if (!type.equals(COMMENT) && !type.equals(LIKE_COMMENT) && !type.equals(MENTION_COMMENT))
            throw new MismatchedAlarmTypeException();

        final Alarm alarm = Alarm.builder()
                .type(type)
                .agent(getAgent())
                .target(target)
                .post(post)
                .comment(comment)
                .build();

        alarmRepository.save(alarm);
    }

    @Transactional
    public void delete(AlarmType type, Member target, Post post) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndPost(type, getAgent(), target, post);
    }
    @Transactional
    public void delete(AlarmType type, Member target, Comment comment) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndComment(type, getAgent(), target, comment);
    }

    @Transactional
    public void delete(Member target, Follow follow) {
        alarmRepository.deleteByTypeAndAgentAndTargetAndFollow(FOLLOW, getAgent(), target, follow);
    }

    private Member getAgent() {
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(MemberDoesNotExistException::new);
    }
}
