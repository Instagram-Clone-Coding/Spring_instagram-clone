package cloneproject.Instagram.dto.alarm;

import cloneproject.Instagram.dto.member.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
