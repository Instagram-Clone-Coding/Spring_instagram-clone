package cloneproject.Instagram.domain.alarm.repository.querydsl;

import cloneproject.Instagram.domain.alarm.dto.AlarmContentDTO;
import cloneproject.Instagram.domain.alarm.dto.AlarmDTO;
import cloneproject.Instagram.domain.alarm.dto.AlarmFollowDTO;
import cloneproject.Instagram.domain.alarm.dto.AlarmType;
import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.follow.entity.Follow;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import cloneproject.Instagram.domain.story.repository.MemberStoryRedisRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloneproject.Instagram.domain.alarm.entity.QAlarm.alarm;
import static cloneproject.Instagram.domain.follow.entity.QFollow.follow;
import static cloneproject.Instagram.domain.member.entity.QMember.member;
import static cloneproject.Instagram.domain.feed.entity.QPost.post;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class AlarmRepositoryQuerydslImpl implements AlarmRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;
    private final MemberStoryRedisRepository memberStoryRedisRepository;

    @Override
    public Page<AlarmDTO> getAlarmDtoPageByMemberId(Pageable pageable, Long memberId) {
        final List<Alarm> alarms = queryFactory
                .selectFrom(alarm)
                .innerJoin(alarm.agent, member).fetchJoin()
                .leftJoin(alarm.post, post).fetchJoin()
                .where(alarm.target.id.eq(memberId))
                .orderBy(alarm.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final List<Long> agentIds = alarms.stream()
                .filter(a -> a.getType().equals(AlarmType.FOLLOW))
                .map(a -> a.getAgent().getId())
                .collect(Collectors.toList());
        final Map<Long, Follow> followMap = queryFactory
                .from(follow)
                .where(
                        follow.member.id.eq(memberId).and(
                                follow.followMember.id.in(agentIds)
                        )
                )
                .innerJoin(follow.member, member).fetchJoin()
                .innerJoin(follow.followMember, member).fetchJoin()
                .transform(groupBy(follow.followMember.id).as(follow));

        final List<AlarmDTO> content = alarms.stream()
                .map(a -> {
                    if (a.getType().equals(AlarmType.FOLLOW))
                        return new AlarmFollowDTO(a, followMap.containsKey(a.getAgent().getId()));
                    else
                        return new AlarmContentDTO(a);
                })
                .collect(Collectors.toList());

        content.forEach(alarm -> {
            final MemberDTO agent = alarm.getAgent();
            final boolean hasStory = memberStoryRedisRepository.findAllByMemberId(agent.getId()).size() > 0;
            agent.setHasStory(hasStory);
        });

        final long total = queryFactory
                .selectFrom(alarm)
                .where(alarm.target.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
