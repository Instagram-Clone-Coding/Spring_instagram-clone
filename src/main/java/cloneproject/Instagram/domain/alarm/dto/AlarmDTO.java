package cloneproject.Instagram.domain.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import cloneproject.Instagram.domain.member.dto.MemberDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {

    private Long id;
    private String type;
    private String message;
    private MemberDTO agent;
    private LocalDateTime createdDate;
}
