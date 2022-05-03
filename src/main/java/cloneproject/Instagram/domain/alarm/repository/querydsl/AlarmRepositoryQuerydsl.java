package cloneproject.Instagram.domain.alarm.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cloneproject.Instagram.domain.alarm.dto.AlarmDTO;

public interface AlarmRepositoryQuerydsl {
    
    Page<AlarmDTO> getAlarmDtoPageByMemberId(Pageable pageable, Long memberId);
}
