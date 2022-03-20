package cloneproject.Instagram.dto.search;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.entity.hashtag.Hashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHashtagDTO extends SearchDTO{
    
    private String name;
    private Integer postCount;

    @QueryProjection
    public SearchHashtagDTO(String dtype, Long count, Hashtag hashtag){
        super(dtype, count);
        this.name = hashtag.getName();
        this.postCount = hashtag.getCount();
    }

}