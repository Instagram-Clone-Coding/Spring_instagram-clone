package cloneproject.Instagram.dto.member;

import java.util.Date;

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
public class JwtDto {
    @ApiModelProperty(value = "토큰 타입", example = "Bearer")
    private String type;
    @ApiModelProperty(value = "Access 토큰", example = "AAA.BBB.CCC")
    private String accessToken;
    @ApiModelProperty(value = "Refresh 토큰", example = "AAA.BBB.CCC")
    private String refreshToken;
    @ApiModelProperty(value = "Access 토큰 만료시간", example = "2021-12-26 10:00")
    private String accessTokenExpires;
}
