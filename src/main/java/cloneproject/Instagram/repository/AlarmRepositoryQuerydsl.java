package cloneproject.Instagram.repository;

import java.util.List;

import cloneproject.Instagram.dto.alarm.AlarmDTO;

public interface AlarmRepositoryQuerydsl {
    
    public List<AlarmDTO> getAlarms(Long loginedMemberId);

}
