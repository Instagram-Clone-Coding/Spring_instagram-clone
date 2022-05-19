package cloneproject.Instagram.domain.alarm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.alarm.dto.AlarmDto;

public interface AlarmRepositoryQuerydsl {
    
    Page<AlarmDto> getAlarmDtoPageByMemberId(Pageable pageable, Long memberId);

}
