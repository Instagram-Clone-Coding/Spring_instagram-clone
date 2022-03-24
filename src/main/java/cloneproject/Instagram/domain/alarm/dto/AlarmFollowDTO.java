package cloneproject.Instagram.domain.alarm.dto;

import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmFollowDTO extends AlarmDTO {

    private boolean isFollowing;

    public AlarmFollowDTO(Alarm alarm, boolean isFollowing) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MemberDTO(alarm.getAgent()), alarm.getCreatedDate());
        this.isFollowing = isFollowing;
    }
}
