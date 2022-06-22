package cloneproject.Instagram.domain.alarm.dto;

import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.member.dto.MemberDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmContentDto extends AlarmDto {

	private Long postId;
	private String postImageUrl;
	private String content;

	public AlarmContentDto(Alarm alarm) {
		super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MemberDto(alarm.getAgent()),
			alarm.getCreatedDate());
		this.postId = alarm.getPost().getId();
		this.postImageUrl = alarm.getPost().getPostImages().get(0).getImage().getImageUrl();
		this.content = alarm.getPost().getContent();
	}

}
