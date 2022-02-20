package cloneproject.Instagram.dto.alarm;

import cloneproject.Instagram.dto.member.MenuMemberDTO;
import cloneproject.Instagram.entity.alarms.Alarm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlarmContentDTO extends AlarmDTO {

    private Long postId;
    private String postImageUrl;
    private String content;

    public AlarmContentDTO(Alarm alarm) {
        super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MenuMemberDTO(alarm.getAgent()), alarm.getCreatedDate());
        this.postId = alarm.getPost().getId();
        this.postImageUrl = alarm.getPost().getPostImages().get(0).getImage().getImageUrl();
        this.content = alarm.getPost().getContent();
    }
}
