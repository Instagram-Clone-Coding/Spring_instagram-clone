package cloneproject.Instagram.domain.story.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberStoryDto {

    private Long seenId;
    private List<StoryDto> stories = new ArrayList<>();
}
