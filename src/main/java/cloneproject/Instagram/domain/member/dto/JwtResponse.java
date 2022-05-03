package cloneproject.Instagram.domain.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("JWT 토큰 응답 데이터 모델")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

	@ApiModelProperty(value = "토큰 타입", example = "Bearer")
	private String type;

	@ApiModelProperty(value = "Access 토큰", example = "AAA.BBB.CCC")
	private String accessToken;

}
