package cloneproject.Instagram.domain.alarm.repository.querydsl;

import static cloneproject.Instagram.domain.alarm.entity.QAlarm.*;
import static cloneproject.Instagram.domain.feed.entity.QPost.*;
import static cloneproject.Instagram.domain.member.entity.QMember.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import cloneproject.Instagram.domain.alarm.entity.Alarm;

@RequiredArgsConstructor
public class AlarmRepositoryQuerydslImpl implements AlarmRepositoryQuerydsl {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Alarm> findAlarmPageByMemberId(Pageable pageable, Long memberId) {
		final List<Alarm> alarms = queryFactory
			.selectFrom(alarm)
			.innerJoin(alarm.agent, member).fetchJoin()
			.innerJoin(alarm.post, post).fetchJoin()
			.where(alarm.target.id.eq(memberId))
			.orderBy(alarm.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		final long total = queryFactory
			.selectFrom(alarm)
			.where(alarm.target.id.eq(memberId))
			.fetchCount();

		return new PageImpl<>(alarms, pageable, total);
	}

}
