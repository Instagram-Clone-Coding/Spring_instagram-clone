package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.alarm.AlarmContentDTO;
import cloneproject.Instagram.dto.alarm.AlarmFollowDTO;
import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.entity.alarms.Alarm;
import cloneproject.Instagram.entity.member.Follow;
import com.querydsl.jpa.impl.JPAQueryFactory;

import cloneproject.Instagram.dto.alarm.AlarmDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cloneproject.Instagram.entity.alarms.QAlarm.alarm;
import static cloneproject.Instagram.entity.member.QFollow.follow;
import static cloneproject.Instagram.entity.member.QMember.member;
import static cloneproject.Instagram.entity.post.QPost.post;
import static cloneproject.Instagram.entity.post.QPostImage.postImage;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class AlarmRepositoryQuerydslImpl implements AlarmRepositoryQuerydsl {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AlarmDTO> getAlarmDtoPageByMemberId(Pageable pageable, Long memberId) {
        final List<Alarm> alarms = queryFactory
                .selectFrom(alarm)
                .innerJoin(alarm.agent, member).fetchJoin()
                .leftJoin(alarm.post, post).fetchJoin()
                .leftJoin(post.postImages, postImage).fetchJoin()
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
                .transform(groupBy(follow.followMember.id).as(follow));

        final List<AlarmDTO> content = alarms.stream()
                .map(a -> {
                    if (a.getType().equals(AlarmType.FOLLOW))
                        return new AlarmFollowDTO(a, followMap.containsKey(a.getAgent().getId()));
                    else
                        return new AlarmContentDTO(a);
                })
                .collect(Collectors.toList());

        final long total = queryFactory
                .selectFrom(alarm)
                .where(alarm.target.id.eq(memberId))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
