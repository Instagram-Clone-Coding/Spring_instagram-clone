package cloneproject.Instagram.dto;

import javax.validation.constraints.NotBlank;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReissueRequest {

    @NotBlank(message = "유효한 Access Token이 없습니다")
    private String accessToken;

    @NotBlank(message = "유효한 Refresh Token이 없습니다")
    private String refreshToken;

}
