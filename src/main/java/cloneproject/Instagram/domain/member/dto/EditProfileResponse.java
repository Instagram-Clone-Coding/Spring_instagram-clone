package cloneproject.Instagram.domain.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@ApiModel("유저 정보 수정 응답 모델")
@Getter
@Builder
@AllArgsConstructor
public class EditProfileResponse {

	@ApiModelProperty(value = "유저네임", example = "dlwlrma")
	String memberUsername;

	@ApiModelProperty(value = "프로필사진 URL", example = "https://drive.google.com/file/d/1Gu0DcGCJNs4Vo0bz2U9U6v01d_VwKijs/view?usp=sharing")
	String memberImageUrl;

	@ApiModelProperty(value = "이름", example = "이지금")
	String memberName;

	@ApiModelProperty(value = "웹사이트", example = "http://localhost:8080")
	String memberWebsite;

	@ApiModelProperty(value = "소개", example = "안녕하세요")
	String memberIntroduce;

	@ApiModelProperty(value = "이메일", example = "aaa@gmail.com")
	String memberEmail;

	@ApiModelProperty(value = "전화번호", example = "010-0000-0000")
	String memberPhone;

	@ApiModelProperty(value = "성별", example = "여성")
	String memberGender;

}
