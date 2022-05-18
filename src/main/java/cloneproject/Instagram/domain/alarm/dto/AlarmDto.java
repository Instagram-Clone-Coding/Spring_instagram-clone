package cloneproject.Instagram.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import cloneproject.Instagram.domain.member.dto.MemberDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDto {

    private Long id;
    private String type;
    private String message;
    private MemberDto agent;
    private LocalDateTime createdDate;

}
