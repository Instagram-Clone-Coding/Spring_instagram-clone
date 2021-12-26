package cloneproject.Instagram.dto.member;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReissueRequest {

    @ApiModelProperty(value = "Access 토큰", example = "AAA.BBB.CCC", required = true)
    @NotBlank(message = "유효한 Access Token이 없습니다")
    private String accessToken;

    @ApiModelProperty(value = "Refresh 토큰", example = "AAA.BBB.CCC", required = true)
    @NotBlank(message = "유효한 Refresh Token이 없습니다")
    private String refreshToken;

}
