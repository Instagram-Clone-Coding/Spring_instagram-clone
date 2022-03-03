package cloneproject.Instagram.dto.hashtag;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HashtagDTO {

    private String name;
    private Integer count;

    public HashtagDTO(Hashtag hashtag) {
        this.name = hashtag.getName();
        this.count = hashtag.getCount();
    }
}
