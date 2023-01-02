package cloneproject.Instagram.domain.search.dto;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class RecommendMemberDto {

	Long memberId;

	Long postCounts;

	@QueryProjection
	public RecommendMemberDto(Long memberId, Long postCounts) {
		this.memberId = memberId;
		this.postCounts = postCounts;
	}

}
