package cloneproject.Instagram.domain.hashtag.dto;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import lombok.Getter;

@Getter
public class HashtagDto {

    private String name;
    private Integer count;

    @QueryProjection
    public HashtagDto(Hashtag hashtag) {
        this.name = hashtag.getName();
        this.count = hashtag.getCount();
    }

}
