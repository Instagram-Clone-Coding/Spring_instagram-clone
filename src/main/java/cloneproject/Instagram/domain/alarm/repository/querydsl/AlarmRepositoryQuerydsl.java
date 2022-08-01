package cloneproject.Instagram.domain.alarm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.alarm.entity.Alarm;

public interface AlarmRepositoryQuerydsl {

	Page<Alarm> findAlarmPageByMemberId(Pageable pageable, Long memberId);

}
