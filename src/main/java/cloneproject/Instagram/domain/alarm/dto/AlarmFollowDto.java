package cloneproject.Instagram.domain.alarm.dto;

import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmFollowDto extends AlarmDto {

	private boolean isFollowing;

	public AlarmFollowDto(Alarm alarm, boolean isFollowing) {
		super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MemberDto(alarm.getAgent()),
			alarm.getCreatedDate());
		this.isFollowing = isFollowing;
	}

}
