package cloneproject.Instagram.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cloneproject.Instagram.entity.alarms.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryQuerydsl{
    
    void deleteAllByTargetId(Long targetId);

}
