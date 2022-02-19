package cloneproject.Instagram.dto.alarm;

import cloneproject.Instagram.dto.member.MenuMemberDTO;
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
    private MenuMemberDTO agent;
    private LocalDateTime createdDate;
}
