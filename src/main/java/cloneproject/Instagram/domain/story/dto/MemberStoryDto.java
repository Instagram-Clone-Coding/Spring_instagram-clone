package cloneproject.Instagram.domain.story.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberStoryDto {

	private Long seenId;
	private List<StoryDto> stories = new ArrayList<>();

}
