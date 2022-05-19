package cloneproject.Instagram.domain.search.dto;

import com.querydsl.core.annotations.QueryProjection;

import cloneproject.Instagram.domain.hashtag.entity.Hashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHashtagDto extends SearchDto {
    
    private String name;
    private Integer postCount;

    @QueryProjection
    public SearchHashtagDto(String dtype, Hashtag hashtag){
        super(dtype);
        this.name = hashtag.getName();
        this.postCount = hashtag.getCount();
    }

}