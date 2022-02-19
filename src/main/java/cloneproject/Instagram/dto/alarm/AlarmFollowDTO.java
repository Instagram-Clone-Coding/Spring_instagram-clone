package cloneproject.Instagram.dto.alarm;

import cloneproject.Instagram.dto.member.MenuMemberDTO;
import cloneproject.Instagram.entity.alarms.Alarm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmFollowDTO extends AlarmDTO {

    private boolean isFollowing;

    public AlarmFollowDTO(Alarm alarm, boolean isFollowing) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MenuMemberDTO(alarm.getAgent()), alarm.getCreatedDate());
        this.isFollowing = isFollowing;
    }
}
