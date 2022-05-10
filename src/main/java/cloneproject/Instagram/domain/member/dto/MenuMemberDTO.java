package cloneproject.Instagram.domain.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("상단 메뉴 유저 프로필 모델")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuMemberDTO {

	@ApiModelProperty(value = "DB상 유저의 ID(PK)", example = "1")
	private Long memberId;

	@ApiModelProperty(value = "유저네임", example = "dlwlrma")
	private String memberUsername;

	@ApiModelProperty(value = "프로필사진 URL", example = "https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing")
	private String memberImageUrl;

	@ApiModelProperty(value = "이름", example = "이지금")
	private String memberName;

}
