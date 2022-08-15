package cloneproject.Instagram.domain.alarm.dto;

import static cloneproject.Instagram.domain.alarm.dto.AlarmType.*;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

import cloneproject.Instagram.domain.alarm.entity.Alarm;
import cloneproject.Instagram.domain.member.dto.MemberDto;

@Getter
@NoArgsConstructor
public class AlarmContentDto extends AlarmDto {

	private Long postId;
	private String postImageUrl;
	private String content;
	private List<String> mentionsOfContent = new ArrayList<>();
	private List<String> hashtagsOfContent = new ArrayList<>();

	public AlarmContentDto(Alarm alarm) {
		super(alarm.getId(), alarm.getType().name(), alarm.getType().getMessage(), new MemberDto(alarm.getAgent()),
			alarm.getCreatedDate());
		this.postId = alarm.getPost().getId();
		this.postImageUrl = alarm.getPost().getPostImages().get(0).getImage().getImageUrl();
		this.content = getContent(alarm);
	}

	private String getContent(Alarm alarm) {
		final AlarmType type = alarm.getType();
		if (type.equals(COMMENT) || type.equals(LIKE_COMMENT) || type.equals(MENTION_COMMENT)) {
			return alarm.getComment().getContent();
		} else if (type.equals(LIKE_POST) || type.equals(MENTION_POST)) {
			return alarm.getPost().getContent();
		}
		return "";
	}

	public void setMentionsOfContent(List<String> mentionsOfContent) {
		this.mentionsOfContent = mentionsOfContent;
	}

	public void setHashtagsOfContent(List<String> hashtagsOfContent) {
		this.hashtagsOfContent = hashtagsOfContent;
	}

}
