package cloneproject.Instagram.domain.alarm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
import cloneproject.Instagram.domain.member.exception.MemberDoesNotExistException;
import cloneproject.Instagram.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;

import java.time.LocalDateTime;
import java.util.List;

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
