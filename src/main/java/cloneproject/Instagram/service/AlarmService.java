package cloneproject.Instagram.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloneproject.Instagram.dto.alarm.AlarmDTO;
import cloneproject.Instagram.dto.alarm.AlarmType;
import cloneproject.Instagram.entity.alarms.Alarm;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.repository.AlarmRepository;
import cloneproject.Instagram.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
    
    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<AlarmDTO> getAlarms(){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AlarmDTO> result = alarmRepository.getAlarms(Long.valueOf(memberId));
        // alarmRepository.deleteAllByTargetId(Long.valueOf(memberId));
        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }

    @Transactional
    public void alert(AlarmType type, Member target, Long itemId){
        final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member agent = memberRepository.findById(Long.valueOf(memberId))
                        .orElseThrow(MemberDoesNotExistException::new);
        Alarm alarm = Alarm.builder()
                            .type(type)
                            .agent(agent)
                            .target(target)
                            .itemId(itemId)
                            .build();
        alarmRepository.save(alarm);
    }

}
