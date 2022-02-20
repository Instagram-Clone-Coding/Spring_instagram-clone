package cloneproject.Instagram.repository;

import cloneproject.Instagram.dto.alarm.AlarmDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlarmRepositoryQuerydsl {
    
    Page<AlarmDTO> getAlarmDtoPageByMemberId(Pageable pageable, Long memberId);
}
