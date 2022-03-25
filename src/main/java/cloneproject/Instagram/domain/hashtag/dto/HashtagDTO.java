package cloneproject.Instagram.domain.hashtag.dto;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HashtagDTO {

    private String name;
    private Integer count;

    @QueryProjection
    public HashtagDTO(Hashtag hashtag) {
        this.name = hashtag.getName();
        this.count = hashtag.getCount();
    }
}
